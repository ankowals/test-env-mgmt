package org.example.environment.aws.commands.rds;

import org.example.environment.aws.DBInstanceStatus;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.RestoreDbInstanceFromDbSnapshotRequest;
import java.time.Duration;

import static org.awaitility.Awaitility.await;

public class AmazonRDSRestoreAndAwait implements AmazonRDSQuery<DBInstance> {

    private final Duration timeout;
    private final RestoreDbInstanceFromDbSnapshotRequest restoreDBInstanceFromDBSnapshotRequest;

    AmazonRDSRestoreAndAwait(RestoreDbInstanceFromDbSnapshotRequest restoreDBInstanceFromDBSnapshotRequest, Duration timeout) {
        this.timeout = timeout;
        this.restoreDBInstanceFromDBSnapshotRequest = restoreDBInstanceFromDBSnapshotRequest;
    }

    AmazonRDSRestoreAndAwait(RestoreDbInstanceFromDbSnapshotRequest restoreDBInstanceFromDBSnapshotRequest) {
        this(restoreDBInstanceFromDBSnapshotRequest, Duration.ofMinutes(20));
    }

    @Override
    public DBInstance run(RdsClient amazonRDS) {
        if (isDbInstancePresent(amazonRDS)) {
            DBInstance dbInstance = RdsDescribe.dbInstance(restoreDBInstanceFromDBSnapshotRequest.dbInstanceIdentifier()).run(amazonRDS);

            if (dbInstance.dbInstanceStatus().equals(DBInstanceStatus.Available.toString()))
                return dbInstance;

            if (dbInstance.dbInstanceStatus().equals(DBInstanceStatus.Deleting.toString()))
                waitUntilDbIsRemoved(amazonRDS);
        }

        RdsRestore.dbInstance(restoreDBInstanceFromDBSnapshotRequest).run(amazonRDS);
        RdsAwait.dbInstance(restoreDBInstanceFromDBSnapshotRequest.dbInstanceIdentifier()).run(amazonRDS);

        return RdsDescribe.dbInstance(restoreDBInstanceFromDBSnapshotRequest.dbInstanceIdentifier()).run(amazonRDS);
    }

    private void waitUntilDbIsRemoved(RdsClient amazonRDS) {
        await().atMost(timeout)
                .with().pollInterval(Duration.ofMinutes(1))
                .ignoreExceptions()
                .until(() -> isDbInstanceNotPresent(amazonRDS));
    }

    private Boolean isDbInstancePresent(RdsClient amazonRDS) {
        return RdsDescribe.dbInstances().run(amazonRDS)
                .stream()
                .anyMatch(e -> e.dbInstanceIdentifier().equals(restoreDBInstanceFromDBSnapshotRequest.dbInstanceIdentifier()));
    }

    private Boolean isDbInstanceNotPresent(RdsClient amazonRDS) {
        return RdsDescribe.dbInstances().run(amazonRDS)
                .stream()
                .noneMatch(e -> e.dbInstanceIdentifier().equals(restoreDBInstanceFromDBSnapshotRequest.dbInstanceIdentifier()));
    }
}
