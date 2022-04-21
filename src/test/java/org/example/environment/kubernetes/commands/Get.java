package org.example.environment.kubernetes.commands;

import org.example.environment.kubernetes.KubernetesJobStatus;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.api.model.networking.v1beta1.Ingress;
import io.fabric8.kubernetes.api.model.policy.v1.PodDisruptionBudget;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.example.environment.kubernetes.KubernetesJobStatus.*;

@Slf4j
public class Get {

    public static KubernetesQuery<List<Namespace>> namespaces() {
        log.info("Get namespaces");
        return client -> client.namespaces()
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<Pod>> pods(Namespace namespace) {
        log.info("Get pods in namespace " + namespace.getMetadata().getName());
        return client -> client.pods()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    @SafeVarargs
    public static KubernetesQuery<List<Pod>> pods(Namespace namespace, Predicate<Pod>... predicates) {
        return client -> Get.pods(namespace).run(client)
                .stream()
                .filter(Arrays.stream(predicates).reduce(Predicate::and).orElse(x -> true))
                .collect(Collectors.toList());
    }

    public static KubernetesQuery<List<Deployment>> deployments(Namespace namespace) {
        log.info("Get deployments in namespace " + namespace.getMetadata().getName());
        return client -> client.apps().deployments()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<ConfigMap>> configMaps(Namespace namespace) {
        log.info("Get configMaps in namespace " + namespace.getMetadata().getName());
        return client -> client.configMaps()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<Secret>> secrets(Namespace namespace) {
        log.info("Get secrets in namespace " + namespace.getMetadata().getName());
        return client -> client.secrets()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<ReplicaSet>> replicaSets(Namespace namespace) {
        log.info("Get replicaSets in namespace " + namespace.getMetadata().getName());
        return client -> client.apps().replicaSets()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<PodDisruptionBudget>> podDisruptionBudgets(Namespace namespace) {
        log.info("Get podDisruptionBudgets in namespace " + namespace.getMetadata().getName());
        return client -> client.policy().v1().podDisruptionBudget()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<Ingress>> ingresses(Namespace namespace) {
        log.info("Get ingresses in namespace " + namespace.getMetadata().getName());
        return client -> client.network().ingresses()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<Service>> services(Namespace namespace) {
        log.info("Get services in namespace " + namespace.getMetadata().getName());
        return client -> client.services()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<List<Endpoints>> endpoints(Namespace namespace) {
        log.info("Get endpoints in namespace " + namespace.getMetadata().getName());
        return client -> client.endpoints()
                .inNamespace(namespace.getMetadata().getName())
                .list()
                .getItems();
    }

    public static KubernetesQuery<String> log(Pod pod) {
        log.info("Get logs from pod " + pod.getMetadata().getName() + " in namespace " + pod.getMetadata().getNamespace());
        return client -> {
            StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());

            pod.getSpec().getContainers()
                    .forEach(container -> {
                        stringJoiner.add("Logs from container with name " + container.getName());
                        stringJoiner.add(client.pods()
                            .inNamespace(pod.getMetadata().getNamespace())
                            .withName(pod.getMetadata().getName())
                            .inContainer(container.getName())
                            .getLog());
                    });

            return stringJoiner.toString();
        };
    }

    public static KubernetesQuery<KubernetesJobStatus> status(Job job) {
        log.info("Get status of job " + job.getMetadata().getName() + " in namespace " + job.getMetadata().getNamespace());
        return client -> {
            JobStatus jobStatus = client.batch().v1()
                    .jobs()
                    .inNamespace(job.getMetadata().getNamespace())
                    .withName(job.getMetadata().getName())
                    .get()
                    .getStatus();

            if (jobStatus.getSucceeded() != null && jobStatus.getSucceeded() == 1)
                return SUCCEEDED;

            if (jobStatus.getFailed() != null && jobStatus.getFailed() == 1)
                return FAILED;

            if (jobStatus.getActive() != null && jobStatus.getActive() == 1)
                return ACTIVE;

            return UNKNOWN;
        };
    }

    public static KubernetesQuery<String> status(Pod pod) {
        log.info("Get status of pod " + pod.getMetadata().getName() + " in namespace " + pod.getMetadata().getNamespace());
        return client -> client
                    .pods()
                    .inNamespace(pod.getMetadata().getNamespace())
                    .withName(pod.getMetadata().getName())
                    .get()
                    .getStatus()
                    .getPhase();
    }
}
