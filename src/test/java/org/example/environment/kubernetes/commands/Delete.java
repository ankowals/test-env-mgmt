package org.example.environment.kubernetes.commands;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentListBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetListBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.IngressListBuilder;
import io.fabric8.kubernetes.api.model.policy.v1.PodDisruptionBudgetListBuilder;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

@Slf4j
public class Delete {

    public static KubernetesCommand resource(File file, Namespace namespace) {
        log.info("Delete resource " + file.getAbsolutePath() + " in " + namespace.getMetadata().getName());
        return client -> client.resourceList(client.load(new FileInputStream(file)).get())
                .inNamespace(namespace.getMetadata().getName())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand namespaces(Namespace... namespace) {
        Arrays.stream(namespace).forEach(n -> log.info("Delete namespaces " + n.getMetadata().getName()));
        return client -> client.resourceList(new NamespaceListBuilder().withItems(Arrays.asList(namespace)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand deployments(Namespace namespace) {
        log.info("Delete deployments in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new DeploymentListBuilder().withItems(Get.deployments(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand configMaps(Namespace namespace) {
        log.info("Delete configMaps in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new ConfigMapListBuilder().withItems(Get.configMaps(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand pods(Namespace namespace) {
        log.info("Delete pods in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new PodListBuilder().withItems(Get.pods(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand secrets(Namespace namespace) {
        log.info("Delete secrets in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new SecretListBuilder().withItems(Get.secrets(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand replicaSets(Namespace namespace) {
        log.info("Delete replicaSets in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new ReplicaSetListBuilder().withItems(Get.replicaSets(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand podDisruptionBudgets(Namespace namespace) {
        log.info("Delete podDisruptionBudgets in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new PodDisruptionBudgetListBuilder().withItems(Get.podDisruptionBudgets(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand ingresses(Namespace namespace) {
        log.info("Delete ingresses in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new IngressListBuilder().withItems(Get.ingresses(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand services(Namespace namespace) {
        log.info("Delete services in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new ServiceListBuilder().withItems(Get.services(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }

    public static KubernetesCommand endpoints(Namespace namespace) {
        log.info("Delete endpoints in namespace " + namespace.getMetadata().getName());
        return client -> client.resourceList(new EndpointsListBuilder().withItems(Get.endpoints(namespace).run(client)).build())
                .withGracePeriod(0)
                .delete();
    }
}
