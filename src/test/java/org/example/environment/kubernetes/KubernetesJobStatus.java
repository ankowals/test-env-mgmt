package org.example.environment.kubernetes;

public enum KubernetesJobStatus {

    SUCCEEDED("Succeeded"),
    FAILED("Failed"),
    ACTIVE("Active"),
    UNKNOWN("Unknown");

    private final String value;

    KubernetesJobStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
