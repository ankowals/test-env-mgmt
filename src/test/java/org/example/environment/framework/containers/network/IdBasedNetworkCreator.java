package org.example.environment.framework.containers.network;

import org.example.environment.framework.conf.BasePropertiesMapper;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.containers.Network;
import org.testcontainers.shaded.com.google.common.base.Strings;

public class IdBasedNetworkCreator implements NetworkCreator {

    private final String networkId;

    public IdBasedNetworkCreator(String networkId) {
        this.networkId = networkId;
    }

    @Override
    public <T extends BasePropertiesMapper> Network create(T mapper) {
        return new Network() {
            @Override
            public String getId() {
                return networkId;
            }

            @Override
            public void close() {

            }

            @Override
            public Statement apply(Statement statement, Description description) {
                return null;
            }
        };
    }

    @Override
    public boolean shouldCreate() {
        return !Strings.isNullOrEmpty(networkId);
    }
}
