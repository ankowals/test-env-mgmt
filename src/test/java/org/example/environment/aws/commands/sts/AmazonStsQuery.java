package org.example.environment.aws.commands.sts;

import software.amazon.awssdk.services.sts.StsClient;

public interface AmazonStsQuery<T> {
    T run(StsClient amazonSts);
}
