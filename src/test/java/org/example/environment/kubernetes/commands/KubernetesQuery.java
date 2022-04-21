package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.client.KubernetesClient;

public interface KubernetesQuery<T> {
    T run(KubernetesClient client);
}
