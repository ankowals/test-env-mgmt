package org.example.environment.creators.commands;

import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Namespace;
import org.example.environment.aws.commands.rds.AmazonRDSQuery;
import org.example.environment.aws.commands.rds.RdsRestore;
import org.example.environment.conf.PropertiesMapper;
import org.example.environment.kubernetes.commands.CreateOrReplace;
import org.example.environment.kubernetes.commands.KubernetesCommand;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.RestoreDbInstanceFromDbSnapshotRequest;
import java.io.IOException;
import java.nio.file.Files;

public class Prepare {

    public static AmazonRDSQuery<DBInstance> testDatabase(PropertiesMapper propertiesMapper) {
        return RdsRestore.dbInstanceAndAwait(RestoreDbInstanceFromDbSnapshotRequest.builder()
                .dbName(propertiesMapper.getAwsRdsDbName())
                .dbInstanceIdentifier(propertiesMapper.getAwsRdsDbInstanceIdentifier())
                .dbSnapshotIdentifier(propertiesMapper.getAwsRdsDbSnapshotIdentifier())
                .dbInstanceClass(propertiesMapper.getAwsRdsDbInstanceClass())
                .dbSubnetGroupName(propertiesMapper.getAwsRdsDbSubnetGroupName())
                .optionGroupName(propertiesMapper.getAwsRdsOptionGroup())
                .dbInstanceClass(propertiesMapper.getAwsRdsDbInstanceClass())
                .build());
    }

    public static KubernetesCommand testMap(Namespace namespace) throws IOException {
        return CreateOrReplace.configMap(new ConfigMapBuilder()
                .withNewMetadata()
                .withName("test-map")
                .withNamespace(namespace.getMetadata().getName())
                .endMetadata()
                .addToData(ImmutableMap.of("test", new String(Files.readAllBytes(ResourceUtils.getFile("classpath:test/test").toPath()))))
                .build());
    }

    public static KubernetesCommand testJob(PropertiesMapper propertiesMapper) {
        return new DeployTestJob(propertiesMapper);
    }
}
