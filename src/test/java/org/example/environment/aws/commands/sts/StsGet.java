package org.example.environment.aws.commands.sts;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;
import java.time.Instant;

@Slf4j
public class StsGet {

    public static AmazonStsQuery<AwsSessionToken> sessionToken() {
        log.info("STS get session token");
        return amazonSts -> {
            GetSessionTokenResponse tokenResponse = amazonSts.getSessionToken(GetSessionTokenRequest.builder()
                    .durationSeconds(3600)
                    .build());

            return new AwsSessionToken(tokenResponse.credentials().expiration(),
                    tokenResponse.credentials().accessKeyId(),
                    tokenResponse.credentials().secretAccessKey(),
                    tokenResponse.credentials().sessionToken());
        };
    }

    @Getter
    public static class AwsSessionToken {

        private final Instant tokenExpiration;
        private final String accessKeyId;
        private final String secretAccessKey;
        private final String sessionToken;

        AwsSessionToken(Instant tokenExpiration, String accessKeyId, String secretAccessKey, String sessionToken) {
            this.tokenExpiration = tokenExpiration;
            this.accessKeyId = accessKeyId;
            this.secretAccessKey = secretAccessKey;
            this.sessionToken = sessionToken;
        }
    }
}
