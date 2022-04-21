package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.List;

import static org.example.environment.framework.utils.ThrowingConsumer.unchecked;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class Await {

    public static KubernetesCommand toBeFinished(Job job) {
        return new AwaitForJob(job);
    }

    public static KubernetesCommand toBeRunning(Pod pod) {
        return toBeRunning(pod, Duration.ofMinutes(20));
    }

    public static KubernetesCommand toBeRunning(Pod pod, Duration timeout) {
        log.info("Await for pod " + pod.getMetadata().getName() + " to be in state Running");
        return client -> await().atMost(timeout)
                .with().pollInterval(15, SECONDS)
                .ignoreExceptions()
                .until(() -> Get.status(pod).run(client).equals("Running"));
    }

    public static KubernetesCommand toBeRunning(List<Pod> pods) {
        return client -> {
            try {
                pods.parallelStream().forEach(unchecked(pod -> Await.toBeRunning(pod).run(client)));
            } catch (Exception e) {
                Print.logs(pods).run(client);
                throw e;
            }
        };
    }
}
