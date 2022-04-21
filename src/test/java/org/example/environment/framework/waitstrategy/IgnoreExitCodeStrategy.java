package org.example.environment.framework.waitstrategy;

import com.github.dockerjava.api.DockerClient;
import org.testcontainers.containers.startupcheck.StartupCheckStrategy;
import org.testcontainers.utility.DockerStatus;

public class IgnoreExitCodeStrategy extends StartupCheckStrategy {

    @Override
    public StartupStatus checkStartupState(DockerClient dockerClient, String containerId) {
        if (DockerStatus.isContainerStopped(getCurrentState(dockerClient, containerId)))
            return StartupStatus.SUCCESSFUL;
        else
            return StartupStatus.NOT_YET_KNOWN;
    }
}
