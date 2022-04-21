package org.example.environment.framework.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.LogUtils;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

import static org.example.environment.framework.utils.ContainerPredicates.*;

public class DockerClientUtils {

    private static final String RYUK_NAME = "testcontainers-ryuk-" + DockerClientFactory.SESSION_ID;

    public static String getLogs(String containerId){
        return LogUtils.getOutput(getClient(), containerId);
    }

    public static void followOutput(Stream<Container> containers) {
        containers.forEach(container ->
                        LogUtils.followOutput(getClient(), container.getId(), new Slf4jLogConsumer(LoggerFactory.getLogger(DockerClientUtils.class))));
    }

    public static String getNetworkIdOfContainerWithOpenPublicPort(int port) {
        return findContainersWithOpenPublicPort(port)
                .flatMap(container -> Objects.requireNonNull(container.getNetworkSettings())
                        .getNetworks()
                        .keySet()
                        .stream())
                .distinct()
                .findFirst()
                .orElse(null);
    }

    /**
     * TC starts Ryuk at first call to DockerClientFactory which instantiates docker client.
     * This is this call DockerClientFactory.instance().client().
     * It forces TC to start Ryuk.
     * Client instance will be returned only upon successful Ryuk startup.
     * Thus we are going to instantiate docker client and kill Ryuk when it was successfully started.
     */
    public static void killRyukContainer() {
        killContainers(findRyuk());
    }

    public static void killContainers(Stream<Container> containers) {
        containers.parallel()
                .map(Container::getId)
                .forEach(id ->
                        DockerClientFactory.instance().client()
                                .removeContainerCmd(id)
                                .withForce(true)
                                .exec()
                );
    }

    private static Stream<Container> findContainersWithOpenPublicPort(int port) {
        return findRunningContainers()
                .filter(hasAnyNetwork())
                .filter(hasOpenPort(port));
    }

    public static Stream<Container> findContainersBelongingToNetwork(String network) {
        return findRunningContainers()
                .filter(hasAnyNetwork())
                .filter(hasValidNetworkSettings(network));
    }

    public static Stream<Container> findRunningContainers() {
        return getClient().listContainersCmd()
                .withStatusFilter(Collections.singletonList("running"))
                .exec()
                .stream();
    }

    private static Stream<Container> findRyuk() {
        return findRunningContainers()
                .filter(hasName(RYUK_NAME));
    }

    private static DockerClient getClient() {
        return DockerClientFactory.instance().client();
    }
}
