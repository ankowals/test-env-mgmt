package org.example.environment.shell;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ShellExecutorResult {

    private final String stdout;
    private final String stderr;
    private final Integer exitCode;

}