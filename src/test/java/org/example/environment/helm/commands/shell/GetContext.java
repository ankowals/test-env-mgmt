package org.example.environment.helm.commands.shell;

import org.example.environment.shell.ShellExecutor;
import org.example.environment.shell.ShellQuery;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetContext implements ShellQuery<List<String>>  {

    @Override
    public List<String> query(ShellExecutor executor) throws Exception {
        return Arrays.stream(executor.execute(cmd(), Duration.ofMinutes(1)).getStdout().split(System.lineSeparator()))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private String cmd() {
        return "kubectl config get-contexts -o name";
    }
}
