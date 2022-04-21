package org.example.environment.kubernetes;

public class KubernetesException extends RuntimeException {

    public KubernetesException(String message) {
        super(message);
    }

    public KubernetesException(String message, Throwable cause) {
        super(message, cause);
    }

}
