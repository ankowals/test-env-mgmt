package org.example.environment.aws.commands.rds;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.rds.model.DeleteDbInstanceRequest;

@Slf4j
public class RdsDelete {

    public static AmazonRDSCommand dbInstance(String awsRdsDbInstanceIdentifier) {
        log.info("RDS deleting dbInstance " + awsRdsDbInstanceIdentifier);
        return amazonRDS -> amazonRDS.deleteDBInstance(DeleteDbInstanceRequest.builder()
                .dbInstanceIdentifier(awsRdsDbInstanceIdentifier)
                .skipFinalSnapshot(true)
                .build());
    }
}
