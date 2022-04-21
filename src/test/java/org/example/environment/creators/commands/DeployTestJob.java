package org.example.environment.creators.commands;

import org.example.environment.conf.PropertiesMapper;
import org.example.environment.kubernetes.commands.Await;
import org.example.environment.kubernetes.commands.CreateOrReplace;
import org.example.environment.kubernetes.commands.KubernetesCommand;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;

import static io.qala.datagen.RandomShortApi.english;

public class DeployTestJob implements KubernetesCommand {

    private static final String CPU = "cpu";
    private static final String MEMORY = "memory";

    private final Job job;

    DeployTestJob(Job job) {
        this.job = job;
    }

    public DeployTestJob(PropertiesMapper propertiesMapper) {
        this.job = createJob(propertiesMapper, createAffinityRequest());
    }

    @Override
    public void run(KubernetesClient client) throws Exception {
        CreateOrReplace.job(job).run(client);
        Await.toBeFinished(job).run(client);
    }

    private Job createJob(PropertiesMapper propertiesMapper, Map<String, Quantity> affinityRequest) {
        return new JobBuilder()
                .withApiVersion("batch/v1")
                .withNewMetadata()
                    .withName("test-job-" + english(3).toLowerCase())
                    .withNamespace(propertiesMapper.getKubernetesNamespace())
                    .withLabels(Collections.singletonMap("app", "test-job"))
                .endMetadata()
                .withNewSpec()
                    .withTtlSecondsAfterFinished(60)
                    .withBackoffLimit(0)
                    .withNewTemplate()
                        .withNewSpec()
                            .addNewContainer()
                                .withName("test-job")
                                .withImage("my-test-image:version")
                                .withResources(new ResourceRequirementsBuilder().withRequests(affinityRequest).withLimits(affinityRequest).build())
                            .endContainer()
                            .withRestartPolicy("Never")
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();
    }

    private Map<String, Quantity> createAffinityRequest() {
        return ImmutableMap.of(
                MEMORY, new Quantity("2G"),
                CPU, new Quantity("1500m"));
    }

    private Affinity createAffinity(String key, String operator, String values) {
        return new Affinity(createNodeAffinity(createNodeSelector(key, operator, values)),null,null);
    }

    private NodeAffinity createNodeAffinity(NodeSelectorTerm nodeSelectorTerm) {
        return new AffinityBuilder().withNewNodeAffinity()
                .withNewRequiredDuringSchedulingIgnoredDuringExecution()
                .withNodeSelectorTerms(nodeSelectorTerm)
                .endRequiredDuringSchedulingIgnoredDuringExecution()
                .endNodeAffinity()
                .buildNodeAffinity();
    }

    private NodeSelectorTerm createNodeSelector(String key, String operator, String values) {
        NodeSelectorTerm nodeSelectorTerm = new NodeSelectorTerm();
        nodeSelectorTerm.setMatchExpressions(Collections.singletonList(new NodeSelectorRequirementBuilder()
                .withKey(key)
                .withOperator(operator)
                .withValues(values)
                .build()
        ));

        return nodeSelectorTerm;
    }
}
