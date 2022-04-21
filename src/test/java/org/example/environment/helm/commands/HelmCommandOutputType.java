package org.example.environment.helm.commands;

public enum HelmCommandOutputType {

    JSON("json"),
    YAML("yaml"),
    TABLE("table");

    private final String value;

    HelmCommandOutputType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
