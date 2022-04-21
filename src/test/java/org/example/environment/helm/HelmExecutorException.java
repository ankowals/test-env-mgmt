package org.example.environment.helm;

public class HelmExecutorException extends RuntimeException {

    public HelmExecutorException(String message) {
        super(message);
    }

    public HelmExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

}
