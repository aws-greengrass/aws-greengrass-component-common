/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

public enum DependencyType {
    HARD,
    SOFT;

    public static DependencyType fromString(String s) {
        return DependencyType.valueOf(s.toUpperCase());
    }
}
