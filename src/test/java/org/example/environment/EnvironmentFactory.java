package org.example.environment;

import org.example.environment.creators.LocalCreator;
import org.example.environment.creators.AwsCreator;
import org.example.environment.framework.creators.TestEnvironmentCreator;
import java.util.HashMap;
import java.util.Map;

import static org.example.environment.EnvironmentType.*;

public class EnvironmentFactory {

    static Map<String, TestEnvironmentCreator> MAPPING = new HashMap<>();

    static {
        MAPPING.put(LOCAL.getType(), new LocalCreator());
        MAPPING.put(AWS.getType(), new AwsCreator());
    }

    public static TestEnvironmentCreator getCreator(String type) {
              return MAPPING.get(type);
    }
}
