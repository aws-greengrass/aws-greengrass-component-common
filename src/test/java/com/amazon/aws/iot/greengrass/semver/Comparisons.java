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

public class Comparisons {
    public static final String[][] COMPARISONS = {
            {"0.0.0", "0.0.0-foo"},
            {"0.0.1", "0.0.0"},
            {"1.0.0", "0.9.9"},
            {"0.10.0", "0.9.0"},
            {"0.99.0", "0.10.0"},
            {"2.0.0", "1.2.3"},
            {"1.2.3", "1.2.3-asdf"},
            {"1.2.3", "1.2.3-4"},
            {"1.2.3", "1.2.3-4-foo"},
            {"1.2.3-5-foo", "1.2.3-5"},
            {"1.2.3-5", "1.2.3-4"},
            {"1.2.3-5-foo", "1.2.3-5-Foo"},
            {"3.0.0", "2.7.2+asdf"},
            {"1.2.3-a.10", "1.2.3-a.5"},
            {"1.2.3-a.b", "1.2.3-a.5"},
            {"1.2.3-a.b", "1.2.3-a"},
            {"1.2.3-a.b.c.10.d.5", "1.2.3-a.b.c.5.d.100"},
            {"1.2.3-r2", "1.2.3-r100"},
            {"1.2.3-r100", "1.2.3-R2"}
    };
}
