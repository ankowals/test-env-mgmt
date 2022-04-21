package org.example.environment.framework;

import org.example.environment.framework.conf.BasePropertiesMapper;

public interface PreparesEnvironment {
    <T extends BasePropertiesMapper> void prepare(T mapper) throws Exception;
}
