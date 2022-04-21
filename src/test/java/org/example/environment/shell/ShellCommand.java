package org.example.environment.shell;

import java.io.IOException;

public interface ShellCommand {
    ShellExecutorResult run(ShellExecutor executor) throws IOException, InterruptedException;
}
