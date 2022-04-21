package org.example.base;

import org.aeonbits.owner.ConfigCache;
import org.example.environment.EnvironmentSetup;
import org.example.environment.conf.PropertiesMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    static {
        PropertiesMapper propertiesMapper = ConfigCache.getOrCreate(PropertiesMapper.class);
        try {
            new EnvironmentSetup().prepare(propertiesMapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    static void setup() {
        System.out.println("setup");
    }

    @AfterAll
    static void teardown() {
        System.out.println("teardown");
    }


}
