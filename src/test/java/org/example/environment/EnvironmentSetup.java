package org.example.environment;

import org.example.environment.conf.PropertiesMapper;
import org.example.environment.conf.PropertiesValidator;
import org.example.environment.framework.PreparesEnvironment;
import org.example.environment.framework.conf.BasePropertiesMapper;

public class EnvironmentSetup implements PreparesEnvironment {

    @Override
    public <T extends BasePropertiesMapper> void prepare(T mapper) throws Exception {
        PropertiesValidator.with((PropertiesMapper) mapper).validate();
        EnvironmentFactory.getCreator(mapper.getEnvironmentType()).create(mapper);
    }
}
