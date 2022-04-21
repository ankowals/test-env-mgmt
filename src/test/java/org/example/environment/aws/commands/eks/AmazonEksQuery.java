package org.example.environment.aws.commands.eks;

import software.amazon.awssdk.services.eks.EksClient;

public interface AmazonEksQuery<T> {
    T run(EksClient amazonEks);
}
