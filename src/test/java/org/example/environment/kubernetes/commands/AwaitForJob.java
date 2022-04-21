package org.example.environment.kubernetes.commands;

import org.example.environment.kubernetes.KubernetesJobStatus;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static org.example.environment.kubernetes.KubernetesJobStatus.FAILED;
import static org.example.environment.kubernetes.KubernetesJobStatus.SUCCEEDED;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class AwaitForJob implements KubernetesCommand {

    private final Job job;
    private final Namespace namespace;

    AwaitForJob(Job job) {
        this.job = job;
        this.namespace = new NamespaceBuilder()
                .withNewMetadata()
                .withName(job.getMetadata().getNamespace())
                .endMetadata()
                .build();
    }

    @Override
    public void run(KubernetesClient client) throws Exception {
        log.info("Await for job " + job.getMetadata().getName());
        try {
            await().atMost(20, MINUTES)
                    .with().pollInterval(5, SECONDS)
                    .until(isFinished(client));
        } catch (Exception e) {
            printLogsFromPods(client);
            throw e;
        }
    }

    private void printLogsFromPods(KubernetesClient client) throws Exception {
        List<Pod> pods = Get.pods(namespace, belongingToJob(job)).run(client);
        Print.logs(pods).run(client);
    }

    private Callable<Boolean> isFinished(KubernetesClient client) {
        return () -> {
            KubernetesJobStatus jobStatus = Get.status(job).run(client);
            return jobStatus.equals(SUCCEEDED) || jobStatus.equals(FAILED);
        };
    }

    private Predicate<Pod> belongingToJob(Job job) {
        return pod -> pod.getMetadata().getLabels().containsKey("job-name")
                && pod.getMetadata().getLabels().containsValue(job.getMetadata().getName());
    }
}