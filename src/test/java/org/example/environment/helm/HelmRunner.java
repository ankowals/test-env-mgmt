package org.example.environment.helm;

import org.example.environment.helm.commands.shell.GetContext;
import org.example.environment.helm.commands.shell.UpdateKubeConfig;
import org.example.environment.shell.*;
import java.io.IOException;

/*
 Internally helm uses kubectl to execute its operations thus we need to add cluster-context if not available
 */
public class HelmRunner {

    private final ShellExecutor executor;
    public HelmRunner(ShellExecutor executor) {
        this.executor = executor;
    }

    public HelmRunner(ShellExecutor executor, String clusterName, String awsRegion) throws Exception {
        this(executor);
        if (!isContextAvailable(clusterName))
            new UpdateKubeConfig(clusterName, awsRegion).run(executor);
    }

    public <T extends ShellCommand> void run(T helmCommand) throws IOException, InterruptedException {
        ShellExecutorResult result = helmCommand.run(executor);
        if (result.getExitCode() != 0)
            throw new HelmExecutorException("Failed to execute helm command, cmd: " + helmCommand + System.lineSeparator() + result.getStderr());
    }

    public <T> T query(ShellQuery<T> helmQuery) throws Exception {
        return helmQuery.query(executor);
    }

    private boolean isContextAvailable(String clusterName) throws Exception {
        return new GetContext().query(executor)
                .stream()
                .anyMatch(context -> context.endsWith(clusterName));
    }
}


