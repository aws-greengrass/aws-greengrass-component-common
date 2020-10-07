package com.amazon.aws.iot.greengrass.configuration.common;

public enum FailureHandlingPolicy {
    ROLLBACK,
    DO_NOTHING;

    public static FailureHandlingPolicy fromString(String s) {
        return FailureHandlingPolicy.valueOf(s.toUpperCase());
    }
}
