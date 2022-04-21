package org.example.environment.aws.commands.rds;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.RestoreDbInstanceFromDbSnapshotRequest;

@Slf4j
public class RdsRestore {

    public static AmazonRDSCommand dbInstance(RestoreDbInstanceFromDbSnapshotRequest restoreDBInstanceFromDBSnapshotRequest) {
        log.info("RDS restoring dbInstance " + restoreDBInstanceFromDBSnapshotRequest.dbInstanceIdentifier());
        return amazonRDS -> amazonRDS.restoreDBInstanceFromDBSnapshot(restoreDBInstanceFromDBSnapshotRequest);
    }

    public static AmazonRDSQuery<DBInstance> dbInstanceAndAwait(RestoreDbInstanceFromDbSnapshotRequest restoreDBInstanceFromDBSnapshotRequest) {
        return new AmazonRDSRestoreAndAwait(restoreDBInstanceFromDBSnapshotRequest);
    }
}
