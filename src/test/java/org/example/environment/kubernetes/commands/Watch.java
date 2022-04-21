package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.api.model.Namespace;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Watch {

    public static KubernetesCommand pods(Namespace namespace) {
        log.info("Watch pods in namespace " + namespace.getMetadata().getName() + " until all running within 15 minutes");
        return client -> Get.pods(namespace).run(client)
                .stream()
                .map(pod -> pod.getMetadata().getName())
                .forEach(name -> {
                    client.pods()
                            .inNamespace(namespace.getMetadata().getName())
                            .withName(name)
                            .watchLog(System.out);

                    client.pods()
                            .inNamespace(namespace.getMetadata().getName())
                            .withName(name)
                            .waitUntilCondition(r -> r.getStatus().getPhase().equals("Running"), 15, TimeUnit.MINUTES);
                });
    }
}
