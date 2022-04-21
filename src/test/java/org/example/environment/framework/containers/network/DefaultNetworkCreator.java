package org.example.environment.framework.containers.network;

import org.example.environment.framework.conf.BasePropertiesMapper;
import org.testcontainers.containers.Network;

public class DefaultNetworkCreator implements NetworkCreator {

    @Override
    public <T extends BasePropertiesMapper> Network create(T mapper) {
        return Network.SHARED;
    }

    @Override
    public boolean shouldCreate() {
        return true;
    }
}
