package org.example.environment.framework.waitstrategy;

import lombok.NonNull;
import org.rnorth.ducttape.TimeoutException;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.rnorth.ducttape.unreliables.Unreliables.retryUntilSuccess;

public class FileWaitStrategy extends AbstractWaitStrategy {

    @NonNull private final Path pathToFile;
    private String stringToFind;

    public FileWaitStrategy(Path pathToFile, Duration timeout) {
        this.pathToFile = Objects.requireNonNull(pathToFile);
        this.startupTimeout = timeout;
    }

    public FileWaitStrategy(Path pathToFile, Duration timeout, String stringToFind) {
        this(pathToFile, timeout);
        this.stringToFind = stringToFind;
    }

    @Override
    protected void waitUntilReady() {
        try {
            retryUntilSuccess((int) startupTimeout.getSeconds(), TimeUnit.SECONDS, () -> {
                getRateLimiter().doWhenReady(() -> {
                    try {
                        File file = pathToFile.toFile();
                        if(!file.exists())
                            throw new FileNotFoundException();

                        if(isWaitingForString()) {
                            if(!file.canRead())
                                throw new RuntimeException(String.format("File `%s` cannot be read", file.getName()));

                            if(!FileUtils.readFileToString(file, Charset.defaultCharset()).contains(stringToFind))
                                throw new RuntimeException(String.format("String `%s` not found in `%s` file", stringToFind, file.getName()));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                return true;
            });
        } catch (TimeoutException e) {
            if(isWaitingForString()) {
                throw new ContainerLaunchException(String.format("Timed out waiting for file `%s` or content `%s`", pathToFile, stringToFind));
            }
            else {
                throw new ContainerLaunchException(String.format("Timed out waiting for file `%s` to be accessible", pathToFile));
            }
        }
    }

    private boolean isWaitingForString(){
        return stringToFind != null;
    }
}
