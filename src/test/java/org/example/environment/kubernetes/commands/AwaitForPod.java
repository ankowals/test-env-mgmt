package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class AwaitForPod implements KubernetesCommand {

    private final Pod pod;

    AwaitForPod(Pod pod) {
        this.pod = pod;
    }

    @Override
    public void run(KubernetesClient client) {
        log.info("Await for pod " + pod.getMetadata().getName());
        try {
            await().atMost(10, MINUTES)
                    .with().pollInterval(15, SECONDS)
                    .until(isRunning(client));
        } catch (Exception e) {
            printLogsFromPods(client);
            throw e;
        }
    }

    private void printLogsFromPods(KubernetesClient client) {
        Namespace namespace = new NamespaceBuilder()
                .withNewMetadata()
                .withNamespace(pod.getMetadata().getNamespace())
                .endMetadata()
                .build();

        Get.pods(namespace)
                .run(client)
                .stream()
                //.filter(pod -> pod.getMetadata().getLabels().containsValue(this.pod.getMetadata().getName()))
                .forEach(pod -> log.info(Get.log(pod).run(client)));
    }

    private Callable<Boolean> isRunning(KubernetesClient client) {
        return () -> {
            String podStatus = Get.status(pod).run(client);
            return podStatus.equals("Running");
        };
    }
}