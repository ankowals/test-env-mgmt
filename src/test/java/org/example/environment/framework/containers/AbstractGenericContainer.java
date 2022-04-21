package org.example.environment.framework.containers;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import java.util.Objects;

import static org.example.environment.framework.utils.EnvironmentUtils.createContainerNamePrefix;
import static org.example.environment.framework.utils.EnvironmentUtils.updateAndGetDockerLabelsMap;
import static io.qala.datagen.RandomShortApi.english;

public abstract class AbstractGenericContainer extends GenericContainer<AbstractGenericContainer> {

    public static final String JAVA_OPTIONS_ENV = "_JAVA_OPTIONS";
    public static final String JAVA_DOCKER_SETTINGS = "-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1"; //in java 11 cgroups limits are respected by default
    public static final String DOCKER_RANDOMIZER_SETTINGS = "-Djava.security.egd=file:/dev/../dev/urandom";
    public static final String DOCKER_MAX_RAM_JVM_SETTINGS = "-XX:MaxRAMPercentage=100"; //by default only 25% of available memory will be allocated
    public static final String DOCKER_MIN_RAM_JVM_SETTINGS = "-XX:MinRAMPercentage=25";

    protected AbstractGenericContainer(@NonNull String image, @NonNull Network network, @NonNull Long memoryLimit, @NonNull String labelKey, @NonNull String labelValue, @NonNull String prefix, @NonNull String infix) {
        super(image);
        withCreateContainerCmdModifier(cmd -> {
            Objects.requireNonNull(cmd.getHostConfig()).withMemory(memoryLimit * FileUtils.ONE_GB).withMemorySwap(0L);
            if (createContainerNamePrefix(prefix).length() > 0)
                cmd.withName(createRandomizedDockerName(prefix, infix, labelValue));
            })
              .withLabels(updateAndGetDockerLabelsMap(labelKey, labelValue, createContainerNamePrefix(prefix)))
              .withNetwork(network);
    }

    private String createRandomizedDockerName(String prefix, String infix, String labelValue) {
        return createContainerNamePrefix(prefix) + infix + labelValue + "-" + english(11);
    }
}