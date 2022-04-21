package org.example.environment.aws.tokens;

import io.fabric8.kubernetes.client.OAuthTokenProvider;
import lombok.SneakyThrows;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Base64;

public class AwsEksTokenProvider implements OAuthTokenProvider {

    private AwsEksToken awsEksToken;
    private final String clusterName;
    private final Region awsRegion;
    private final AwsSessionCredentialsProvider awsSessionCredentialsProvider;

    public AwsEksTokenProvider(String clusterName, AwsSessionCredentialsProvider awsSessionCredentialsProvider, String awsRegion) {
        this.clusterName = clusterName;
        this.awsSessionCredentialsProvider = awsSessionCredentialsProvider;
        this.awsRegion = Region.of(awsRegion);
    }

    @SneakyThrows
    @Override
    public String getToken() {
        if (isExpired())
            awsEksToken = generateToken();

        return awsEksToken.getTokenValue();
    }

    private boolean isExpired() {
        return awsEksToken == null || awsEksToken.getTokenExpiration().isBefore(Instant.now().minusSeconds(60));
    }

    private AwsEksToken generateToken() throws URISyntaxException {
        ZonedDateTime expirationDate = LocalDateTime.now().atZone(ZoneId.of("UTC")).plusSeconds(60);

        String tokenValue = getAuthenticationToken(awsSessionCredentialsProvider, expirationDate, awsRegion);
        Instant tokenExpiration = expirationDate.toInstant();

        return new AwsEksToken(tokenValue, tokenExpiration);
    }

    private String getAuthenticationToken(AwsSessionCredentialsProvider awsSessionCredentialsProvider, ZonedDateTime expirationDate, Region awsRegion) throws URISyntaxException {
        SdkHttpFullRequest signedRequest = Aws4Signer.create()
                    .presign(createRequest(awsRegion, clusterName), createParams(awsRegion, expirationDate, awsSessionCredentialsProvider));

            String encodedUrl = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signedRequest.getUri().toString().getBytes(StandardCharsets.UTF_8));

            return ("k8s-aws-v1." + encodedUrl);
    }

    private SdkHttpFullRequest createRequest(Region awsRegion, String clusterName) throws URISyntaxException {
        return SdkHttpFullRequest.builder()
                .method(SdkHttpMethod.GET)
                .uri(new URI("https", String.format("sts.%s.amazonaws.com", awsRegion.id()), "/", null))
                .appendHeader("x-k8s-aws-id", clusterName)
                .appendRawQueryParameter("Action", "GetCallerIdentity")
                .appendRawQueryParameter("Version", "2011-06-15")
                .build();
    }

    private Aws4PresignerParams createParams(Region awsRegion, ZonedDateTime expirationDate, AwsSessionCredentialsProvider awsSessionCredentialsProvider) {
        return Aws4PresignerParams.builder()
                .awsCredentials(awsSessionCredentialsProvider.resolveCredentials())
                .signingRegion(awsRegion)
                .signingName("sts")
                .signingClockOverride(Clock.systemUTC())
                .expirationTime(expirationDate.toInstant())
                .build();
    }
}
