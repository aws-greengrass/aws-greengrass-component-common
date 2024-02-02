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

public class Equality {
    public static final String[][] EQUALITY = {
            {"1.2.3", "v1.2.3"},
            {"1.2.3", " v1.2.3"},
            {"1.2.3-0", "v1.2.3-0"},
            {"1.2.3-0", " v1.2.3-0"},
            {"1.2.3-1", "v1.2.3-1"},
            {"1.2.3-1", " v1.2.3-1"},
            {"1.2.3-beta", "v1.2.3-beta"},
            {"1.2.3-beta", " v1.2.3-beta"},
            {"1.2.3-beta+build", "1.2.3-beta+otherbuild"},
            {"1.2.3+build", "1.2.3+otherbuild"},
            {"  v1.2.3+build", "1.2.3+otherbuild"}
    };
}
