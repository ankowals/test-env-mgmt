package org.example.environment.aws.tokens;

import java.time.Instant;

public class AwsEksToken {

    private final String tokenValue;
    private final Instant tokenExpiration;

    AwsEksToken(String tokenValue, Instant tokenExpiration) {
        this.tokenValue = tokenValue;
        this.tokenExpiration = tokenExpiration;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Instant getTokenExpiration() {
        return tokenExpiration;
    }
}