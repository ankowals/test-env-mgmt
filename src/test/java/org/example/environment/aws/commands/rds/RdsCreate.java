package org.example.environment.aws.commands.rds;

import org.example.environment.conf.PropertiesMapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.rds.model.CreateDbInstanceRequest;

@Slf4j
public class RdsCreate {

    public static AmazonRDSCommand dbInstance(CreateDbInstanceRequest createDBInstanceRequest) {
        log.info("RDS creating dbInstance " + createDBInstanceRequest.dbInstanceIdentifier());
        return amazonRDS -> amazonRDS.createDBInstance(createDBInstanceRequest);
    }

    public static AmazonRDSCommand dbInstance(PropertiesMapper propertiesMapper) {
        return amazonRDS -> dbInstance(CreateDbInstanceRequest.builder()
                .dbName(propertiesMapper.getAwsRdsDbName())
                .dbInstanceIdentifier(propertiesMapper.getAwsRdsDbInstanceIdentifier())
                .dbInstanceClass(propertiesMapper.getAwsRdsDbInstanceClass())
                .engine(propertiesMapper.getAwsRdsEngineType())
                .engineVersion(propertiesMapper.getAwsRdsEngineVersion())
                .allocatedStorage(propertiesMapper.getAwsRdsDbAllocatedStorage())
                .masterUsername(propertiesMapper.getAwsRdsMasterUsername())
                .masterUserPassword(propertiesMapper.getAwsRdsMasterUserPassword())
                .backupRetentionPeriod(propertiesMapper.getAwsRdsBackupRetentionPeriod())
                .multiAZ(propertiesMapper.getAwsRdsMultiAz())
                .autoMinorVersionUpgrade(propertiesMapper.getAwsRdsAutoMinorVersionUpgrade())
                .licenseModel(propertiesMapper.getAwsRdsLicenseModel())
                .copyTagsToSnapshot(propertiesMapper.getAwsRdsCopyTagsToSnapshot())
                .enablePerformanceInsights(propertiesMapper.getAwsRdsEnablePerformanceInsights())
                .performanceInsightsRetentionPeriod(propertiesMapper.getAwsRdsPerformanceInsightsRetentionPeriod())
                .dbSubnetGroupName(propertiesMapper.getAwsRdsDbSubnetGroupName())
                .build())
                .run(amazonRDS);
    }
}
