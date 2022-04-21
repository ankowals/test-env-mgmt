package org.example.environment.aws.commands.rds;

import org.example.environment.aws.AmazonRdsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import java.util.List;

@Slf4j
public class RdsDescribe {

    public static AmazonRDSQuery<DBInstance> dbInstance(String awsRdsDbInstanceIdentifier) {
        log.info("RDS describing dbInstance " + awsRdsDbInstanceIdentifier);
        return amazonRDS -> amazonRDS.describeDBInstances(DescribeDbInstancesRequest.builder()
                        .dbInstanceIdentifier(awsRdsDbInstanceIdentifier).build())
                        .dbInstances()
                        .stream()
                        .filter(e -> StringUtils.endsWithIgnoreCase(e.dbInstanceIdentifier(), awsRdsDbInstanceIdentifier))
                        .findFirst()
                        .orElseThrow(() -> new AmazonRdsException("Database with dbInstanceIdentifier: " + awsRdsDbInstanceIdentifier + " not found!"));
    }

    public static AmazonRDSQuery<List<DBInstance>> dbInstances() {
        log.info("RDS describing dbInstances");
        return amazonRDS -> amazonRDS.describeDBInstances().dbInstances();
    }
}
