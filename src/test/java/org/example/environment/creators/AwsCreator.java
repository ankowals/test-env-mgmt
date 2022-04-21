package org.example.environment.creators;

import io.fabric8.kubernetes.api.model.*;
import org.assertj.core.util.Strings;
import org.example.environment.aws.AmazonRunner;
import org.example.environment.aws.commands.eks.EksDescribe;
import org.example.environment.aws.commands.rds.RdsDelete;
import org.example.environment.aws.tokens.AwsSessionCredentialsProvider;
import org.example.environment.aws.tokens.ShellEksTokenProvider;
import org.example.environment.conf.PropertiesMapper;
import org.example.environment.creators.commands.Prepare;
import org.example.environment.framework.conf.BasePropertiesMapper;
import org.example.environment.framework.creators.TestEnvironmentCreator;
import org.example.environment.helm.Chart;
import org.example.environment.helm.HelmRunner;
import org.example.environment.helm.commands.HelmList;
import org.example.environment.helm.commands.HelmUninstall;
import org.example.environment.helm.commands.HelmUpgrade;
import org.example.environment.helm.domain.HelmListingMapper;
import org.example.environment.kubernetes.KubernetesRunner;
import org.example.environment.kubernetes.commands.*;
import org.example.environment.shell.ShellExecutor;
import org.springframework.util.ResourceUtils;
import software.amazon.awssdk.services.eks.model.Cluster;
import software.amazon.awssdk.services.rds.model.Endpoint;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.example.environment.framework.utils.UrlUtils.isUrlReachable;

/*
 Set your namespace & cluster name in kubernetes.properties
 Set your db name & db instance identifier in aws-rds.properties
 Set path to your helm file in helm.properties
 Install kubectl, helm and awscli
 */

public class AwsCreator implements TestEnvironmentCreator {

    private AmazonRunner amazon;
    private KubernetesRunner kubernetes;
    private HelmRunner helm;

    private Namespace namespace;
    private Cluster cluster;

    @Override
    public <T extends BasePropertiesMapper> void create(T envConf) throws Exception {
        PropertiesMapper propertiesMapper = (PropertiesMapper) envConf;

        init(propertiesMapper);

        if (isUrlReachable("http://test.example.org"))
            return;

        if (propertiesMapper.isKubernetesEnvironmentPreCleanup())
            deleteNow(propertiesMapper.getAwsRdsDbInstanceIdentifier(), cluster.arn(), propertiesMapper.getHelmReleaseName());

        if (propertiesMapper.isKubernetesEnvironmentPostCleanup())
            deleteOnExit(propertiesMapper.getAwsRdsDbInstanceIdentifier(), cluster.arn(), propertiesMapper.getHelmReleaseName());

        Endpoint endpoint = amazon.run(Prepare.testDatabase(propertiesMapper)).endpoint();

        updateDbConnectionDetails(propertiesMapper, endpoint);

        if (!shouldInstallHelm(propertiesMapper.getHelmChartVersion(), cluster.arn()))
            return;

        File valuesFile = ResourceUtils.getFile("classpath:helm/test-helm-values.yaml");
        File helmFile = ResourceUtils.getFile("classpath:" + propertiesMapper.getHelmChartPath());

        Chart chart = new Chart(helmFile);

        kubernetes.run(CreateOrReplace.namespace(namespace));
        kubernetes.run(Prepare.testMap(namespace));

        helm.run(HelmUpgrade.builder()
                .releaseName(propertiesMapper.getHelmReleaseName())
                .chart(chart)
                .valuesFiles(valuesFile)
                .namespace(namespace)
                .kubeContext(cluster.arn())
                .timeout(Duration.ofMinutes(10))
                .build());

        kubernetes.run(Prepare.testJob(propertiesMapper));

        List<Pod> pods = kubernetes.run(Get.pods(namespace, belongingToRelease(propertiesMapper.getHelmReleaseName())));
        kubernetes.run(Await.toBeRunning(pods));
    }

    private Predicate<Pod> belongingToRelease(String releaseName) {
        return pod -> pod.getMetadata().getName().startsWith(releaseName);
    }

    private boolean shouldInstallHelm(String chartVersion, String kubeContext) throws Exception {
        if (Strings.isNullOrEmpty(chartVersion))
            return true;

        HelmListingMapper helmListingMapper = helm.query(HelmList.builder()
                .namespace(namespace)
                .kubeContext(kubeContext)
                .build());

        if (helmListingMapper.getStatus() == null)
            return true;
        else
            return !helmListingMapper.getStatus().equals("deployed") || !helmListingMapper.getChart().equals(chartVersion);
    }

    private void deleteOnExit(String awsRdsDbInstanceIdentifier, String kubeContext, String helmReleaseName) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                TimeUnit.MINUTES.sleep(1); //we may need a moment to scale down our cluster
            } catch (InterruptedException ignored){}

            deleteNow(awsRdsDbInstanceIdentifier, kubeContext, helmReleaseName);
        }));
    }

    private void deleteNow(String awsRdsDbInstanceIdentifier, String kubeContext, String helmReleaseName) {
        try {
            helm.run(HelmUninstall.builder().releaseName(helmReleaseName).namespace(namespace).kubeContext(kubeContext).build());
        } catch (Throwable ignored) {}

        try {
            kubernetes.run(Delete.namespaces(namespace));
        } catch (Throwable ignored) {}

        try {
            amazon.run(RdsDelete.dbInstance(awsRdsDbInstanceIdentifier));
        } catch (Throwable ignored) {}
    }

    private void init(PropertiesMapper propertiesMapper) throws Exception {
        namespace = new NamespaceBuilder()
                .withNewMetadata()
                .withName(propertiesMapper.getKubernetesNamespace())
                .endMetadata()
                .build();

        AwsSessionCredentialsProvider awsSessionCredentialsProvider = new AwsSessionCredentialsProvider(propertiesMapper.getAwsRdsRegion());

        amazon = new AmazonRunner(awsSessionCredentialsProvider, propertiesMapper.getAwsRdsRegion());

        cluster = amazon.run(EksDescribe.cluster(propertiesMapper.getKubernetesClusterName()));

        kubernetes = new KubernetesRunner(new ShellEksTokenProvider(propertiesMapper.getKubernetesClusterName(), propertiesMapper.getAwsRdsRegion()), cluster.endpoint());
        helm = new HelmRunner(new ShellExecutor(), propertiesMapper.getKubernetesClusterName(), propertiesMapper.getAwsRdsRegion());
    }

    //used by external clients to connect to db
    private void updateDbConnectionDetails(PropertiesMapper propertiesMapper, Endpoint endpoint) {
        propertiesMapper.setProperty("db.host", endpoint.address());
        propertiesMapper.setProperty("db.port", String.valueOf(endpoint.port()));
        propertiesMapper.setProperty("db.service", propertiesMapper.getAwsRdsDbName());
    }
}
