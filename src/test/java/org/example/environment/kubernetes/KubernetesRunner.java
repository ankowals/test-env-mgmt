package org.example.environment.kubernetes;

import org.example.environment.kubernetes.commands.KubernetesCommand;
import org.example.environment.kubernetes.commands.KubernetesQuery;
import io.fabric8.kubernetes.client.*;

public class KubernetesRunner {

    private final OAuthTokenProvider tokenProvider;
    private final String clusterEndPoint;

    public KubernetesRunner(OAuthTokenProvider tokenProvider, String clusterEndPoint) {
        this.tokenProvider = tokenProvider;
        this.clusterEndPoint = clusterEndPoint;
    }

    public void run(KubernetesCommand command) throws Exception {
        try(KubernetesClient client = createKubernetesClient(tokenProvider, clusterEndPoint)) {
            command.run(client);
        }
    }

    public <T> T run(KubernetesQuery<T> command) {
        try(KubernetesClient client = createKubernetesClient(tokenProvider, clusterEndPoint)) {
            return command.run(client);
        }
    }

    private KubernetesClient createKubernetesClient(OAuthTokenProvider tokenProvider, String clusterEndPoint) {
        Config config = new ConfigBuilder().withMasterUrl(clusterEndPoint)
                .withOauthTokenProvider(tokenProvider)
                .withTrustCerts(true)
                .build();

        return new DefaultKubernetesClient(config);
    }
}
