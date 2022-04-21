package org.example.environment.aws.tokens;

import org.example.environment.shell.*;
import io.fabric8.kubernetes.client.OAuthTokenProvider;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.*;

/*
 * In case AwsEksTokenProvider generated tokens do not work use this one.
 * Calls 'aws eks get-token --cluster-name clusterName --region awsRegion' via shell command.
 */
public class ShellEksTokenProvider implements OAuthTokenProvider {

    private AwsEksToken awsEksToken;
    private final String clusterName;
    private final String awsRegion;
    private final ShellExecutor shellExecutor;

    public ShellEksTokenProvider(String clusterName, String awsRegion) {
        this.shellExecutor = new ShellExecutor();
        this.clusterName = clusterName;
        this.awsRegion = awsRegion;
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

    private AwsEksToken generateToken() throws Exception {
        updateClusterConfig(clusterName, awsRegion).run(shellExecutor);
        JSONObject output = getTokenCommand(clusterName, awsRegion).query(shellExecutor);

        Instant tokenExpiration = OffsetDateTime.parse(getExpirationTimestamp(output))
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .toInstant();

        String tokenValue = doGetToken(output);

        return new AwsEksToken(tokenValue, tokenExpiration);
    }

    private ShellQuery<JSONObject> getTokenCommand(String clusterName, String awsRegion) {
        return executor -> {
            String cmd = "aws eks get-token --cluster-name " + clusterName + " --region " + awsRegion + " --output json";

            return new JSONObject(executor.execute(cmd, Duration.ofMinutes(1)).getStdout());
        };
    }

    private ShellCommand updateClusterConfig(String clusterName, String awsRegion) {
        return executor -> executor.execute("aws eks update-kubeconfig --name " + clusterName + " --region " + awsRegion);
    }

    private String getExpirationTimestamp(JSONObject jSONObject) throws JSONException {
        return jSONObject.getJSONObject("status").getString("expirationTimestamp");
    }

    private String doGetToken(JSONObject jSONObject) throws JSONException {
        return jSONObject.getJSONObject("status").getString("token");
    }
}