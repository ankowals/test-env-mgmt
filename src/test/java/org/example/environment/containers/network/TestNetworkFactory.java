package org.example.environment.containers.network;

import org.example.environment.conf.PropertiesMapper;
import org.example.environment.framework.conf.BasePropertiesMapper;
import org.example.environment.framework.containers.network.DefaultNetworkCreator;
import org.example.environment.framework.containers.network.IdBasedNetworkCreator;
import org.example.environment.framework.containers.network.NetworkCreator;
import org.example.environment.framework.containers.network.PrefixedNetworkCreator;
import org.testcontainers.containers.Network;
import java.util.*;

import static org.example.environment.framework.utils.DockerClientUtils.getNetworkIdOfContainerWithOpenPublicPort;
import static org.example.environment.framework.utils.EnvironmentUtils.createContainerNamePrefix;

public class TestNetworkFactory {

    private static final Map<Class<? extends BasePropertiesMapper>, Network> store = new HashMap<>();

    /*
        return same instance of Network
     */
    public static Network createOrGet(PropertiesMapper mapper) {
        synchronized (store) {
            Network result = store.get(mapper.getClass());
            if (Objects.isNull(result)) {
                result = create(mapper);
                store.put(mapper.getClass(), result);
            }
            return result;
        }
    }

    /*
        return unique instance of Network
     */
    public static Network create(PropertiesMapper mapper) {
        return getCreators(mapper).stream()
                .filter(NetworkCreator::shouldCreate)
                .findFirst()
                .orElse(new DefaultNetworkCreator())
                .create(mapper);
    }

    private static List<NetworkCreator> getCreators(PropertiesMapper mapper) {
        return Arrays.asList(
                new IdBasedNetworkCreator(getNetworkIdOfContainerWithOpenPublicPort(mapper.getDbPort())),
                new PrefixedNetworkCreator(createContainerNamePrefix(mapper.getDockerNamePrefix()))
        );
    }
}
