package org.example.environment.framework.conf;

import lombok.extern.slf4j.Slf4j;
import java.util.Properties;

@Slf4j
public class BasePropertiesValidator<T extends BasePropertiesMapper> {

    protected final T mapper;

    protected BasePropertiesValidator(T mapper) {
        this.mapper = mapper;
    }

    protected void print(){
        Properties properties = System.getProperties();
        String[] names = getSystemPropertiesNames();
        log.info("System properties");
        for (String name : names) {
            log.info(name + "=" + properties.get(name));
        }
        log.info("Framework properties");
        log.info("environment.type=" + mapper.getEnvironmentType());
        log.info("docker.containers.cleanup=" + mapper.isContainersCleanupEnabled());
        log.info("Environment variables");
        log.info("DOCKER_HOST=" + System.getenv("DOCKER_HOST"));
    }

    private String[] getSystemPropertiesNames() {
        return new String[]{"os.arch",
                "os.name",
                "user.name",
                "user.home",
                "user.dir",
                "user.timezone",
                "java.runtime.name",
                "java.version",
                "java.vm.version",
                "java.io.tmpdir",
                "java.home"};
    }
}
