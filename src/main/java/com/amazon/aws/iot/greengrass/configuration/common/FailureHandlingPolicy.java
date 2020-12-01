/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.configuration.common;

public enum FailureHandlingPolicy {
    ROLLBACK,
    DO_NOTHING;

    public static FailureHandlingPolicy fromString(String s) {
        return FailureHandlingPolicy.valueOf(s.toUpperCase());
    }
}
