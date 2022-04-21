package org.example.environment.shell;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.LogOutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ShellExecutorLogOutputStream extends LogOutputStream {

    private final List<String> lines = new ArrayList<>();

    ShellExecutorLogOutputStream(int logLevel) {
        super(logLevel);
    }

    @Override
    protected void processLine(String line, int level) {
        if ( level == 0 ) {
            log.debug(line);
        } else {
            log.warn(line);
        }
        lines.add(line);
    }

    String getOutput() {
        return String.join(System.lineSeparator(), lines);
    }
}