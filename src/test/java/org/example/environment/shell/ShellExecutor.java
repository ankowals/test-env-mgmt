package org.example.environment.shell;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Vector;

@Slf4j
public class ShellExecutor {

    /**
     * Execute a Command as a blocking process.<br>
     *     Use Start-Process cmd-let to run a powershell command/script in background<br>
     *         For example Start-Process -FilePath 'powershell.exe' -ArgumentList '-command "& {gci c:\ -rec}"'
     *     See more here https://ss64.com/ps/start-process.html<br>
     *     When in bash use nohup command &>/dev/null &<br>
     *     See more here https://ss64.com/bash/nohup.html
     */
    public ShellExecutorResult execute(String cmd, File workingDir, Duration timeout) throws IOException, InterruptedException {
        Executor executor = new DefaultExecutor();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(1000L * timeout.getSeconds());
        ShutdownHookProcessDestroyer processDestroyer = new ShutdownHookProcessDestroyer(); //This is used to end the process when the JVM exits

        ShellExecutorLogOutputStream os = new ShellExecutorLogOutputStream(0);
        ShellExecutorLogOutputStream es = new ShellExecutorLogOutputStream(1);

        PumpStreamHandler psh = new PumpStreamHandler(os, es);

        executor.setStreamHandler(psh);
        executor.setWatchdog(watchdog);
        executor.setProcessDestroyer(processDestroyer);
        executor.setWorkingDirectory(workingDir);

        CommandLine fixedCmd = fixCommandLine(CommandLine.parse(cmd));

        log.info("Executing " + fixedCmd);

        executor.execute(fixedCmd, resultHandler);
        resultHandler.waitFor();

        return new ShellExecutorResult(os.getOutput(), es.getOutput(), resultHandler.getExitValue());
    }

    public ShellExecutorResult execute(String cmd, Duration timeout) throws IOException, InterruptedException {
        return execute(cmd, new File(System.getProperty("java.io.tmpdir")), timeout);
    }

    public ShellExecutorResult execute(String cmd) throws IOException, InterruptedException {
        return execute(cmd, Duration.ofMinutes(15));
    }

    private CommandLine fixCommandLine(CommandLine badCommandLine) {
        try {
            CommandLine fixedCommandLine = new CommandLine(badCommandLine.getExecutable());
            fixedCommandLine.setSubstitutionMap(badCommandLine.getSubstitutionMap());
            Vector<?> arguments = (Vector<?>) FieldUtils.readField(badCommandLine, "arguments", true);
            arguments.stream()
                    .map(badArgument -> {
                        try {
                            return (String) FieldUtils.readField(badArgument, "value", true);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .forEach(goodArgument -> fixedCommandLine.addArgument(goodArgument, false));
            return fixedCommandLine;
        } catch (Exception e) {
            log.warn("Cannot fix command line " + e.getMessage());
            return badCommandLine;
        }
    }

}