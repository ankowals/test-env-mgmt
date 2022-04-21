package org.example.environment.containers;

import org.example.environment.framework.containers.AbstractGenericContainer;
import org.example.environment.framework.containers.RunnableContainer;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import java.util.Objects;

import static org.example.environment.framework.utils.Durations.TIMEOUT_5_MIN;

public class DbContainer extends AbstractGenericContainer implements RunnableContainer {

    public static final int ORACLE_LISTENER_PORT = 1521;
    public static final String HOST_NETWORK_ALIAS = "oracle";
    public static final String ORACLE_DB_SERVICE = "xp";
    private static final String LABEL_VALUE = "DbContainer";
    private static final Long MEMORY_LIMIT = 4L;

    public DbContainer(String image, Network network, String labelKey, String prefix, String infix) {
        super(image, network, MEMORY_LIMIT, labelKey, LABEL_VALUE, prefix, infix);
        withNetworkAliases(HOST_NETWORK_ALIAS)
                .withEnv(JAVA_OPTIONS_ENV, JAVA_DOCKER_SETTINGS + " " + DOCKER_RANDOMIZER_SETTINGS)
                .withExposedPorts(ORACLE_LISTENER_PORT)
                .waitingFor(Wait.forLogMessage(".*DATABASE IS OPEN.*", 1)
                        .withStartupTimeout(TIMEOUT_5_MIN));
    }

    public DbContainer(String image, Network network, String labelKey, String prefix, String infix, int port) {
        this(image, network, labelKey, prefix, infix);
        withCreateContainerCmdModifier(cmd ->
                Objects.requireNonNull(cmd.getHostConfig())
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(ORACLE_LISTENER_PORT))));
    }

    public String getJdbcUrl() {
        return "jdbc:oracle:thin:@"
                + getContainerIpAddress()
                + ":" + getMappedPort(ORACLE_LISTENER_PORT)
                + "/" + ORACLE_DB_SERVICE
                + "?oracle.net.disableOob=true"; //https://github.com/oracle/docker-images/issues/1663
    }

    @Override
    public void run() {
        start();
    }
}
