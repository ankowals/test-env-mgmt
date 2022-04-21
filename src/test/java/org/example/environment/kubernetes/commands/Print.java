package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class Print {

    public static KubernetesCommand pods(Namespace namespace) {
        return client -> Get.pods(namespace).run(client)
                .stream()
                .map(pod -> pod.getMetadata().getName())
                .forEach(name -> log.info("Pod with name " + name + " found in namespace " + namespace.getMetadata().getName()));
    }

    public static KubernetesCommand logs(List<Pod> pods) {
        return client -> pods.forEach(pod -> log.info(Get.log(pod).run(client)));
    }
}
