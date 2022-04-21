package org.example.environment.aws.commands.rds;

import org.example.environment.aws.DBInstanceStatus;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;

import static org.awaitility.Awaitility.await;

@Slf4j
public class RdsAwait {

    public static AmazonRDSCommand dbInstance(String awsRdsDbInstanceIdentifier) {
        log.info("RDS awaiting for dbInstance " + awsRdsDbInstanceIdentifier);
        return amazonRDS -> await().atMost(Duration.ofMinutes(20))
                .with().pollInterval(Duration.ofMinutes(1))
                .ignoreExceptions()
                .until(() -> RdsDescribe.dbInstance(awsRdsDbInstanceIdentifier).run(amazonRDS).dbInstanceStatus().equals(DBInstanceStatus.Available.toString()));
    }
}
