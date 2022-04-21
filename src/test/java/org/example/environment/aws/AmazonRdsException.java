package org.example.environment.aws;

public class AmazonRdsException extends RuntimeException {

    public AmazonRdsException(String message) {
        super(message);
    }

    public AmazonRdsException(String message, Throwable cause) {
        super(message, cause);
    }
}
