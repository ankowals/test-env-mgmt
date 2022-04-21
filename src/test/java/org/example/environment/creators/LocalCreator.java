package org.example.environment.creators;

import com.github.dockerjava.api.model.Container;
import org.example.environment.conf.PropertiesMapper;
import org.example.environment.containers.DbContainer;
import org.example.environment.containers.TestContainerFactory;
import org.example.environment.containers.TomcatContainer;
import org.example.environment.framework.conf.BasePropertiesMapper;
import org.example.environment.framework.creators.TestEnvironmentCreator;
import org.testcontainers.shaded.com.google.common.base.Strings;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.example.environment.containers.DbContainer.ORACLE_LISTENER_PORT;
import static org.example.environment.framework.utils.ContainerPredicates.doesNotHaveOpenPort;
import static org.example.environment.framework.utils.ContainerPredicates.hasImageContaining;
import static org.example.environment.framework.utils.DockerClientUtils.*;
import static org.example.environment.framework.utils.EnvironmentUtils.isPortOpen;

public class LocalCreator implements TestEnvironmentCreator {

    //to run containers in parallel use
    //Arrays.asList(dbContainer, tomcatContainer).parallelStream().forEach(RunnableContainer::run);

    @Override
    public <T extends BasePropertiesMapper> void create(T mapper) throws IOException, InterruptedException {
        PropertiesMapper propertiesMapper = (PropertiesMapper) mapper;

        if (!propertiesMapper.isContainersCleanupEnabled())
            killRyukContainer();

        boolean isDbAvailable = isDbAvailable(propertiesMapper);
        TestContainerFactory containerFactory = new TestContainerFactory(propertiesMapper);

        if (!isDbAvailable) {
            DbContainer dbContainer = containerFactory.createDbContainer();
            updateDbDetails(dbContainer, propertiesMapper); //use this to connect from external clients
        }

        //allows streaming logs from already running containers
        if (!propertiesMapper.shouldRestartApplicationContainers() && isDbAvailable) {
            followOutput(findTomcatContainers(propertiesMapper.getDbPort()));

            return; //db & application containers running do nothing more and leave
        }

        stopContainersWithoutOpenPorts(propertiesMapper.getDbPort()); //stop selected containers, for example application only

        TomcatContainer tomcatContainer = containerFactory.createTomcatContainer(); //here we can start application containers again
        tomcatContainer.run();

        updateWebDetails(tomcatContainer, propertiesMapper); //use this to connect from external clients like browsers
    }

    private void stopContainersWithoutOpenPorts(int dbPort) {
        String networkId = getNetworkIdOfContainerWithOpenPublicPort(dbPort);
        Supplier<Stream<Container>> streamSupplier = () -> findContainersBelongingToNetwork(networkId)
                .filter(doesNotHaveOpenPort(dbPort));

        killContainers(streamSupplier.get());
    }

    private Stream<Container> findTomcatContainers(int dbPort) {
        String networkId = getNetworkIdOfContainerWithOpenPublicPort(dbPort);

        if (Strings.isNullOrEmpty(networkId))
            return Stream.empty();

        Supplier<Stream<Container>> streamSupplier = () -> findContainersBelongingToNetwork(networkId)
                .filter(hasImageContaining("tomcat"));

        return streamSupplier.get();
    }

    private void updateDbDetails(DbContainer container, PropertiesMapper propertiesMapper) {
        propertiesMapper.setProperty("db.host", container.getContainerIpAddress());
        propertiesMapper.setProperty("db.port", String.valueOf(container.getMappedPort(ORACLE_LISTENER_PORT)));
        propertiesMapper.setProperty("db.jdbc.url", container.getJdbcUrl());
    }

    private void updateWebDetails(TomcatContainer container, PropertiesMapper propertiesMapper) {
        String testHostIpAddress = container.getContainerIpAddress();

        //browser in container can't access localhost, an actual ip of a test host should be used
        if (testHostIpAddress.equals("localhost"))
            testHostIpAddress = getHostIpAddress();

        propertiesMapper.setProperty("tomcat.host", testHostIpAddress);
        propertiesMapper.setProperty("tomcat.port", String.valueOf(container.getOpenPort()));
    }

    private boolean isDbAvailable(PropertiesMapper propertiesMapper) {
        String host = propertiesMapper.getDbHost();
        int port = propertiesMapper.getDbPort();

        if (isPortOpen(host, port)) {
            updatePropertiesMapper(host, port, propertiesMapper);

            return true;
        }

        return false;
    }

    private String getHostIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Can't find resolvable ip address of a test host! Tests in browser started in a container will fail!"
                    + e.getMessage());
        }
    }

    private void updatePropertiesMapper(String host, int port, PropertiesMapper propertiesMapper) {
        propertiesMapper.setProperty("debug.connectedToExistingDb", Boolean.toString(true));
        propertiesMapper.setProperty("db.jdbc.url", createJdbcUrl(host, port, propertiesMapper));
    }

    private String createJdbcUrl(String host, int port, PropertiesMapper propertiesMapper) {
        return "jdbc:oracle:thin:@" + host + ":" + port + "/" + propertiesMapper.getDbService();
    }
}
