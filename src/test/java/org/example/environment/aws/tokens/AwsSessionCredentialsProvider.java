package org.example.environment.aws.tokens;

import org.example.environment.aws.commands.sts.AmazonStsQuery;
import org.example.environment.aws.commands.sts.StsGet;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;

import java.time.Instant;

public class AwsSessionCredentialsProvider implements AwsCredentialsProvider {

    private StsGet.AwsSessionToken awsSessionToken;
    private final AmazonStsRunner amazonStsRunner;

    public AwsSessionCredentialsProvider(String awsRdsRegion) {
        this.amazonStsRunner = new AmazonStsRunner(awsRdsRegion);
    }

    @Override
    public AwsCredentials resolveCredentials() {
        StsGet.AwsSessionToken token = getToken();

        return AwsSessionCredentials.create(
                token.getAccessKeyId(),
                token.getSecretAccessKey(),
                token.getSessionToken());
    }

    private StsGet.AwsSessionToken getToken() {
        if (isExpired())
            awsSessionToken = generateToken();

        return awsSessionToken;
    }

    private boolean isExpired() {
        return awsSessionToken == null || awsSessionToken.getTokenExpiration().isBefore(Instant.now().minusSeconds(60));
    }

    private StsGet.AwsSessionToken generateToken() {
        return amazonStsRunner.run(StsGet.sessionToken());
    }

    public static class AmazonStsRunner {

        private final Region region;

        AmazonStsRunner(String awsRegion) {
            this.region = Region.of(awsRegion);
        }

        public <T> T run(AmazonStsQuery<T> command) {
            try(StsClient stsClient = createAmazonStsClient(region)) {return command.run(stsClient);}
        }

        private StsClient createAmazonStsClient(Region region) {
            return StsClient.builder()
                    .region(region)
                    .build();
        }
    }
}
