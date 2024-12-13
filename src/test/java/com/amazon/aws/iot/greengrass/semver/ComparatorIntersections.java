/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 *
 * Translated from node-semver:
 *
 * The ISC License
 * Copyright (c) Isaac Z. Schlueter and Contributors
 *
 * See com.amazon.aws.iot.greengrass.semver.THIRD-PARTY-LICENSE
 */

package com.amazon.aws.iot.greengrass.semver;

public class ComparatorIntersections {
    public static final Object[][] COMPARATOR_INTERSECTIONS = {
            // One is a Version
            {"1.3.0", ">=1.3.0", true},
            {"1.3.0", ">1.3.0", false},
            {">=1.3.0", "1.3.0", true},
            {">1.3.0", "1.3.0", false},
            // Same direction increasing
            {">1.3.0", ">1.2.0", true},
            {">1.2.0", ">1.3.0", true},
            {">=1.2.0", ">1.3.0", true},
            {">1.2.0", ">=1.3.0", true},
            // Same direction decreasing
            {"<1.3.0", "<1.2.0", true},
            {"<1.2.0", "<1.3.0", true},
            {"<=1.2.0", "<1.3.0", true},
            {"<1.2.0", "<=1.3.0", true},
            // Different directions, same semver and inclusive operator
            {">=1.3.0", "<=1.3.0", true},
            {">=v1.3.0", "<=1.3.0", true},
            {">=1.3.0", ">=1.3.0", true},
            {"<=1.3.0", "<=1.3.0", true},
            {"<=1.3.0", "<=v1.3.0", true},
            {">1.3.0", "<=1.3.0", false},
            {">=1.3.0", "<1.3.0", false},
            // Opposite matching directions
            {">1.0.0", "<2.0.0", true},
            {">=1.0.0", "<2.0.0", true},
            {">=1.0.0", "<=2.0.0", true},
            {">1.0.0", "<=2.0.0", true},
            {"<=2.0.0", ">1.0.0", true},
            {"<=1.0.0", ">=2.0.0", false},
            {"", "", true},
            {"", ">1.0.0", true},
            {"<=2.0.0", "", true},
    };
}
