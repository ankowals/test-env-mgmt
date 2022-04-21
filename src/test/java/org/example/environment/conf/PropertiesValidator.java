package org.example.environment.conf;

import org.example.environment.EnvironmentType;
import org.example.environment.framework.conf.BasePropertiesValidator;
import org.example.environment.framework.conf.ValidatesProperties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertiesValidator extends BasePropertiesValidator<PropertiesMapper> implements ValidatesProperties {

    protected PropertiesValidator(PropertiesMapper mapper) {
        super(mapper);
    }

    public static PropertiesValidator with(PropertiesMapper mapper) {
        return new PropertiesValidator(mapper);
    }

    @Override
    public void validate(){
        print();
        validateEnvironmentType(mapper.getEnvironmentType());
    }

    private void validateEnvironmentType(String type) {
        boolean isPresent = Stream.of(EnvironmentType.values())
                .anyMatch(t -> t.getType().equals(type));

        if (!isPresent) {
            throw new RuntimeException("Wrong value of environment type provided! Allowed values are " + getEnvTypeValues());
        }
    }

    private String getEnvTypeValues() {
        return Stream.of(EnvironmentType.values())
                .map(EnvironmentType::getType)
                .collect(Collectors.joining(", "));
    }

}
