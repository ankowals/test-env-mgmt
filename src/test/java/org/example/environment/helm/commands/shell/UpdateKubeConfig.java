package org.example.environment.helm.commands.shell;

import org.example.environment.shell.*;
import java.io.IOException;
import java.time.Duration;

public class UpdateKubeConfig implements ShellCommand {

    private final String clusterName;
    private final String awsRegion;

    public UpdateKubeConfig(String clusterName, String awsRegion) {
        this.clusterName = clusterName;
        this.awsRegion = awsRegion;
    }

    @Override
    public ShellExecutorResult run(ShellExecutor executor) throws IOException, InterruptedException {
        return executor.execute(cmd(clusterName, awsRegion), Duration.ofMinutes(1));
    }

    private String cmd(String clusterName, String awsRegion) {
        return "aws eks update-kubeconfig --name " + clusterName + " --region " + awsRegion;
    }
}
