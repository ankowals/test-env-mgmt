package org.example.environment.framework.creators;

import org.example.environment.framework.conf.BasePropertiesMapper;

public interface TestEnvironmentCreator {
    <T extends BasePropertiesMapper> void create(T mapper) throws Exception;
}
