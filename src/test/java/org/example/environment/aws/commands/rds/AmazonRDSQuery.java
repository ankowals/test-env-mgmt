package org.example.environment.aws.commands.rds;

import software.amazon.awssdk.services.rds.RdsClient;

public interface AmazonRDSQuery<T> {
    T run(RdsClient amazonRDS);
}
