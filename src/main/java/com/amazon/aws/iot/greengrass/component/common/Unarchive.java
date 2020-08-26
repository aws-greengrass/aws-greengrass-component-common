package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Unarchive {
    NONE,
    ZIP;

    @JsonCreator
    public static Unarchive fromString(String s) {
        return Unarchive.valueOf(s.toUpperCase());
    }
}
