package org.example.environment.conf;

import org.example.environment.framework.conf.BasePropertiesMapper;
import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "classpath:aws-rds.properties",
        "classpath:kubernetes.properties",
        "classpath:helm.properties"})
public interface PropertiesMapper extends BasePropertiesMapper {

    @Config.Key("tomcat.port")
    int getTomcatPort();

    @Config.Key("tomcat.host")
    String getTomcatHost();

    @Key("db.port")
    int getDbPort();

    @Key("db.host")
    String getDbHost();

    @Key("db.service")
    String getDbService();

    @Key("db.jdbc.url")
    String getDbJdbcUrl();

    @Key("docker.db.container.image")
    String getDockerDbImage();

    @Key("docker.tomcat.container.image")
    String getDockerTomcatImage();

    @Key("docker.db.container.use.fixed.port")
    boolean isFixedPortDbContainer();

    @Config.Key("docker.tomcat.container.use.fixed.port")
    boolean isFixedPortTomcatContainer();

    @Config.Key("aws.rds.db.name")
    @Config.DefaultValue("")
    String getAwsRdsDbName();

    @Config.Key("aws.rds.db.instance.identifier")
    @Config.DefaultValue("")
    String getAwsRdsDbInstanceIdentifier();

    @Config.Key("aws.rds.db.instance.class")
    String getAwsRdsDbInstanceClass();

    @Config.Key("aws.rds.db.allocated.storage")
    Integer getAwsRdsDbAllocatedStorage();

    @Config.Key("aws.rds.db.subnet.group.name")
    String getAwsRdsDbSubnetGroupName();

    @Config.Key("aws.rds.db.snapshot.identifier")
    String getAwsRdsDbSnapshotIdentifier();

    @Config.Key("aws.rds.master.username")
    String getAwsRdsMasterUsername();

    @Config.Key("aws.rds.master.user.password")
    String getAwsRdsMasterUserPassword();

    @Config.Key("aws.rds.engine.type")
    String getAwsRdsEngineType();

    @Config.Key("aws.rds.engine.version")
    String getAwsRdsEngineVersion();

    @Config.Key("aws.rds.license.model")
    String getAwsRdsLicenseModel();

    @Config.Key("aws.rds.backup.retention.period")
    Integer getAwsRdsBackupRetentionPeriod();

    @Config.Key("aws.rds.performance.insights.retention.period")
    Integer getAwsRdsPerformanceInsightsRetentionPeriod();

    @Config.Key("aws.rds.multiaz")
    Boolean getAwsRdsMultiAz();

    @Config.Key("aws.rds.auto.minor.version.upgrade")
    Boolean getAwsRdsAutoMinorVersionUpgrade();

    @Config.Key("aws.rds.copy.tags.to.snapshot")
    Boolean getAwsRdsCopyTagsToSnapshot();

    @Config.Key("aws.rds.enable.performance.insights")
    Boolean getAwsRdsEnablePerformanceInsights();

    @Config.Key("aws.rds.region")
    String getAwsRdsRegion();

    @Config.Key("aws.rds.option.group")
    String getAwsRdsOptionGroup();

    @Config.Key("kubernetes.namespace")
    @Config.DefaultValue("")
    String getKubernetesNamespace();

    @Config.Key("kubernetes.cluster.name")
    String getKubernetesClusterName();

    @Config.Key("kubernetes.environment.post.cleanup")
    boolean isKubernetesEnvironmentPostCleanup();

    @Config.Key("kubernetes.environment.pre.cleanup")
    boolean isKubernetesEnvironmentPreCleanup();

    @Config.Key("helm.chart.path")
    String getHelmChartPath();

    @Config.Key("helm.release.name")
    String getHelmReleaseName();

    @Config.Key("helm.chart.version")
    String getHelmChartVersion();
}
