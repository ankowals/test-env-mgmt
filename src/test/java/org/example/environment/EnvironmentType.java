package org.example.environment;

public enum EnvironmentType {

    LOCAL("local"),
    AWS("aws");

    public final String type;

    public String getType(){
        return type;
    }

    EnvironmentType(String type) {
        this.type = type;
    }
}
