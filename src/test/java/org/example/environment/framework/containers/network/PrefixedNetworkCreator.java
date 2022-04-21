package org.example.environment.framework.containers.network;

import org.example.environment.framework.conf.BasePropertiesMapper;
import org.testcontainers.containers.Network;
import org.testcontainers.shaded.com.google.common.base.Strings;

import static org.example.environment.framework.utils.EnvironmentUtils.createContainerNamePrefix;
import static org.example.environment.framework.utils.EnvironmentUtils.updateAndGetDockerLabelsMap;

public class PrefixedNetworkCreator implements NetworkCreator {

    private final String prefix;

    public PrefixedNetworkCreator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public <T extends BasePropertiesMapper> Network create(T mapper) {
        return Network.NetworkImpl.builder()
                .createNetworkCmdModifier(modifier ->
                      modifier.withName(prefix + mapper.getDockerNetworkSuffix())
                              .withLabels(updateAndGetDockerLabelsMap(mapper.getDockerLabelKey(), "Network", createContainerNamePrefix(prefix)))
                ).build();
    }

    @Override
    public boolean shouldCreate() {
        return !Strings.isNullOrEmpty(prefix);
    }
}
