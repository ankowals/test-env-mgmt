package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.client.KubernetesClient;

public interface KubernetesCommand {
    void run(KubernetesClient client) throws Exception;
}
