package org.example.environment.aws.commands.rds;

import software.amazon.awssdk.services.rds.RdsClient;

public interface AmazonRDSCommand {
    void run(RdsClient amazonRDS);
}
