package org.example.environment.aws;

import org.example.environment.aws.commands.eks.AmazonEksQuery;
import org.example.environment.aws.commands.rds.AmazonRDSCommand;
import org.example.environment.aws.commands.rds.AmazonRDSQuery;
import org.example.environment.aws.tokens.AwsSessionCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.rds.RdsClient;

public class AmazonRunner {

    private final AwsSessionCredentialsProvider awsSessionCredentialsProvider;
    private final Region region;

    public AmazonRunner(AwsSessionCredentialsProvider awsSessionCredentialsProvider, String awsRegion) {
        this.region = Region.of(awsRegion);
        this.awsSessionCredentialsProvider = awsSessionCredentialsProvider;
    }

    public void run(AmazonRDSCommand command) {
        try(RdsClient rdsClient = createAmazonRdsClient(awsSessionCredentialsProvider, region)) {
            command.run(rdsClient);
        }
    }

    public <T> T run(AmazonRDSQuery<T> command) {
        try(RdsClient rdsClient = createAmazonRdsClient(awsSessionCredentialsProvider, region)) {
            return command.run(rdsClient);
        }
    }

    public <T> T run(AmazonEksQuery<T> command) {
        try(EksClient eksClient = createAmazonEksClient(awsSessionCredentialsProvider, region) ) {
            return command.run(eksClient);
        }
    }

    private RdsClient createAmazonRdsClient(AwsCredentialsProvider awsCredentialsProvider, Region region) {
        return RdsClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(region)
                .build();
    }

    private EksClient createAmazonEksClient(AwsCredentialsProvider awsCredentialsProvider, Region region) {
        return EksClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(region)
                .build();
    }
}
