package org.example.environment.containers;

import org.example.environment.conf.PropertiesMapper;
import org.example.environment.containers.network.TestNetworkFactory;
import org.testcontainers.containers.Network;

public class TestContainerFactory {

    PropertiesMapper propertiesMapper;
    Network network;
    String dockerLabelKey;
    String prefix;
    String infix;

    public TestContainerFactory(PropertiesMapper propertiesMapper) {
        this.network = TestNetworkFactory.createOrGet(propertiesMapper);
        this.propertiesMapper = propertiesMapper;
        this.dockerLabelKey = propertiesMapper.getDockerLabelKey();
        this.prefix = propertiesMapper.getDockerNamePrefix();
        this.infix = propertiesMapper.getDockerNameInfix();
    }

    public DbContainer createDbContainer() {
        return propertiesMapper.isFixedPortDbContainer()
                ? new DbContainer(propertiesMapper.getDockerDbImage(), network, dockerLabelKey, prefix, infix, propertiesMapper.getDbPort())
                : new DbContainer(propertiesMapper.getDockerDbImage(), network, dockerLabelKey, prefix, infix);
    }

    public TomcatContainer createTomcatContainer() {
        return propertiesMapper.isFixedPortTomcatContainer()
                ? new TomcatContainer(propertiesMapper.getDockerTomcatImage(), network, dockerLabelKey, prefix, infix, propertiesMapper.getTomcatPort())
                : new TomcatContainer(propertiesMapper.getDockerTomcatImage(), network, dockerLabelKey, prefix, infix);
    }
}
