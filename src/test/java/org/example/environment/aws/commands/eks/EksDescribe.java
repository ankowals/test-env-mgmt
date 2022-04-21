package org.example.environment.aws.commands.eks;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.eks.model.Cluster;
import software.amazon.awssdk.services.eks.model.DescribeClusterRequest;

@Slf4j
public class EksDescribe {

    public static AmazonEksQuery<Cluster> cluster(String clusterName) {
        log.info("EKS describing cluster " + clusterName);
        return amazonEks -> amazonEks.describeCluster(DescribeClusterRequest.builder()
                        .name(clusterName)
                        .build())
                        .cluster();
    }
}
