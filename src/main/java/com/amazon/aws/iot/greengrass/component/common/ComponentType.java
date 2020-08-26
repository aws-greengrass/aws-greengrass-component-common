package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ComponentType {
    LAMBDA, PLUGIN;

    @JsonCreator
    public static ComponentType fromString(String s) {
        return ComponentType.valueOf(s.toUpperCase());
    }
}
