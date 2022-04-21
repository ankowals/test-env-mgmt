package org.example.environment.aws;

public enum DBInstanceStatus {

    Available("available"),
    Modifying("modifying"),
    IncompatibleNetwork("incompatible-network"),
    InsufficientResourceLimits("insufficient-resource-limits"),
    Creating("creating"),
    Deleting("deleting"),
    Suspended("suspended"),
    Suspending("suspending"),
    Reactivating("reactivating");

    private final String value;

    DBInstanceStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
