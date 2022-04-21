package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.*;

import static org.example.environment.framework.utils.ThrowingConsumer.unchecked;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class CreateOrReplace {

    public static KubernetesCommand namespace(Namespace namespace) {
        log.info("Create or replace namespace " + namespace.getMetadata().getName());
        return client -> client.namespaces().createOrReplace(namespace);
    }

    public static KubernetesCommand configMap(ConfigMap configMap) {
        log.info("Create or replace conifgMap " + configMap.getMetadata().getName());
        return client -> client.configMaps()
                .inNamespace(configMap.getMetadata().getNamespace())
                .createOrReplace(configMap);
    }

    public static KubernetesCommand secret(Secret secret) {
        log.info("Create or replace secret " + secret.getMetadata().getName());
        return client -> client.secrets()
                .inNamespace(secret.getMetadata().getNamespace())
                .createOrReplace(secret);
    }

    public static KubernetesCommand secret(String name, Namespace namespace, File... files) {
        Map<String, String> map = new HashMap<>();
        Arrays.asList(files).forEach(unchecked(file ->
                        map.put(file.getName(), new String(Base64.getEncoder().encode(Files.readAllBytes(file.toPath())), UTF_8))));

        return secret(new SecretBuilder()
                .withNewMetadata()
                .withName(name)
                .withNamespace(namespace.getMetadata().getName())
                .endMetadata()
                .addToData(map)
                .build());
    }

    //load yaml into Kubernetes resources and apply it
    public static KubernetesCommand resource(File file, Namespace namespace) {
        log.info("Create or replace resource " + file.getAbsolutePath() + " in " + namespace.getMetadata().getName());
        return client -> client.resourceList(client.load(new FileInputStream(file)).get())
                .inNamespace(namespace.getMetadata().getName())
                .createOrReplace();
    }

    public static KubernetesCommand job(Job job) {
        log.info("Create or replace job " + job.getMetadata().getName());
        return client -> client.batch().v1().jobs()
                .inNamespace(job.getMetadata().getNamespace())
                .createOrReplace(job);
    }
}
