package org.example.environment.framework.conf;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;
import java.util.List;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "classpath:environment.properties",
        "classpath:framework.properties"})
public interface BasePropertiesMapper extends Mutable, Accessible {

    @Key("docker.containers.cleanup")
    boolean isContainersCleanupEnabled();

    @Key("environment.type")
    String getEnvironmentType();

    @Key("docker.name.prefix")
    String getDockerNamePrefix();

    @Key("docker.network.suffix")
    String getDockerNetworkSuffix();

    @Key("docker.name.infix")
    String getDockerNameInfix();

    @Key("docker.label.key")
    String getDockerLabelKey();

    @Key("logger.exclude.prefixes")
    List<String> getLoggerPrefixesToExclude();

    @DefaultValue("true")
    @Key("environment.management.enabled")
    boolean isEnvironmentManagementEnabled();

    @Key("environment.setup.restart.application.containers")
    boolean shouldRestartApplicationContainers();
}
