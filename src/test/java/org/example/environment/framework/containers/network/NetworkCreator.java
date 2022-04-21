package org.example.environment.framework.containers.network;

import org.example.environment.framework.conf.BasePropertiesMapper;
import org.testcontainers.containers.Network;

public interface NetworkCreator {
    <T extends BasePropertiesMapper> Network create(T mapper);
    boolean shouldCreate();
}
