package org.example.environment.containers;

import org.example.environment.framework.containers.AbstractGenericContainer;
import org.example.environment.framework.containers.RunnableContainer;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import lombok.NonNull;
import org.testcontainers.containers.Network;
import java.util.Objects;

import static org.example.environment.framework.utils.Durations.TIMEOUT_2_MIN;

public class TomcatContainer extends AbstractGenericContainer implements RunnableContainer {

    public static final int TOMCAT_CONNECTOR_PORT = 8080;
    private static final String LABEL_VALUE = "TomcatContainer";
    private static final Long MEMORY_LIMIT = 6L;
    public static final String HOST_NETWORK_ALIAS = "tomcat";
    public static final String MANAGER_USER = "admin";
    public static final String MANAGER_PASS = "admin";

    protected TomcatContainer(@NonNull String image, @NonNull Network network, @NonNull String labelKey, @NonNull String prefix, @NonNull String infix) {
        super(image, network, MEMORY_LIMIT, labelKey, LABEL_VALUE, prefix, infix);
        withNetworkAliases(HOST_NETWORK_ALIAS)
                .withEnv(JAVA_OPTIONS_ENV, JAVA_DOCKER_SETTINGS + " " + DOCKER_RANDOMIZER_SETTINGS)
                .withExposedPorts(TOMCAT_CONNECTOR_PORT)
                .withStartupTimeout(TIMEOUT_2_MIN);
    }

    public TomcatContainer(String image, Network network, String labelKey, String prefix, String infix, int port) {
        this(image, network, labelKey, prefix, infix);
        withCreateContainerCmdModifier(cmd ->
                Objects.requireNonNull(cmd.getHostConfig())
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(TOMCAT_CONNECTOR_PORT))));
    }

    public int getOpenPort() {
        return getMappedPort(TOMCAT_CONNECTOR_PORT);
    }

    @Override
    public void run() {
        start();
    }
}
