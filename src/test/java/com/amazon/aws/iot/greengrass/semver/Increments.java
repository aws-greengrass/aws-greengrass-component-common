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

public class Increments {
    public static final String[][] INCREMENTS = {
            {"1.2.3", "major", "2.0.0"},
            {"1.2.3", "minor", "1.3.0"},
            {"1.2.3", "patch", "1.2.4"},
            {"1.2.3-tag", "major", "2.0.0"},
            {"1.2.3", "fake", null},
            {"1.2.0-0", "patch", "1.2.0"},
            {"fake", "major", null},
            {"1.2.3-4", "major", "2.0.0"},
            {"1.2.3-4", "minor", "1.3.0"},
            {"1.2.3-4", "patch", "1.2.3"},
            {"1.2.3-alpha.0.beta", "major", "2.0.0"},
            {"1.2.3-alpha.0.beta", "minor", "1.3.0"},
            {"1.2.3-alpha.0.beta", "patch", "1.2.3"},
            {"1.2.4", "prerelease", "1.2.5-0"},
            {"1.2.3-0", "prerelease", "1.2.3-1"},
            {"1.2.3-alpha.0", "prerelease", "1.2.3-alpha.1"},
            {"1.2.3-alpha.1", "prerelease", "1.2.3-alpha.2"},
            {"1.2.3-alpha.2", "prerelease", "1.2.3-alpha.3"},
            {"1.2.3-alpha.0.beta", "prerelease", "1.2.3-alpha.1.beta"},
            {"1.2.3-alpha.1.beta", "prerelease", "1.2.3-alpha.2.beta"},
            {"1.2.3-alpha.2.beta", "prerelease", "1.2.3-alpha.3.beta"},
            {"1.2.3-alpha.10.0.beta", "prerelease", "1.2.3-alpha.10.1.beta"},
            {"1.2.3-alpha.10.1.beta", "prerelease", "1.2.3-alpha.10.2.beta"},
            {"1.2.3-alpha.10.2.beta", "prerelease", "1.2.3-alpha.10.3.beta"},
            {"1.2.3-alpha.10.beta.0", "prerelease", "1.2.3-alpha.10.beta.1"},
            {"1.2.3-alpha.10.beta.1", "prerelease", "1.2.3-alpha.10.beta.2"},
            {"1.2.3-alpha.10.beta.2", "prerelease", "1.2.3-alpha.10.beta.3"},
            {"1.2.3-alpha.9.beta", "prerelease", "1.2.3-alpha.10.beta"},
            {"1.2.3-alpha.10.beta", "prerelease", "1.2.3-alpha.11.beta"},
            {"1.2.3-alpha.11.beta", "prerelease", "1.2.3-alpha.12.beta"},
            {"1.2.0", "prepatch", "1.2.1-0"},
            {"1.2.0-1", "prepatch", "1.2.1-0"},
            {"1.2.0", "preminor", "1.3.0-0"},
            {"1.2.3-1", "preminor", "1.3.0-0"},
            {"1.2.0", "premajor", "2.0.0-0"},
            {"1.2.3-1", "premajor", "2.0.0-0"},
            {"1.2.0-1", "minor", "1.2.0"},
            {"1.0.0-1", "major", "1.0.0"},

            {"1.2.3", "major", "2.0.0", "dev"},
            {"1.2.3", "minor", "1.3.0", "dev"},
            {"1.2.3", "patch", "1.2.4", "dev"},
            {"1.2.3-tag", "major", "2.0.0", "dev"},
            {"1.2.3", "fake", null, "dev"},
            {"1.2.0-0", "patch", "1.2.0", "dev"},
            {"fake", "major", null, "dev"},
            {"1.2.3-4", "major", "2.0.0", "dev"},
            {"1.2.3-4", "minor", "1.3.0", "dev"},
            {"1.2.3-4", "patch", "1.2.3", "dev"},
            {"1.2.3-alpha.0.beta", "major", "2.0.0", "dev"},
            {"1.2.3-alpha.0.beta", "minor", "1.3.0", "dev"},
            {"1.2.3-alpha.0.beta", "patch", "1.2.3", "dev"},
            {"1.2.4", "prerelease", "1.2.5-dev.0", "dev"},
            {"1.2.3-0", "prerelease", "1.2.3-dev.0", "dev"},
            {"1.2.3-alpha.0", "prerelease", "1.2.3-dev.0", "dev"},
            {"1.2.3-alpha.0", "prerelease", "1.2.3-alpha.1", "alpha"},
            {"1.2.3-alpha.0.beta", "prerelease", "1.2.3-dev.0", "dev"},
            {"1.2.3-alpha.0.beta", "prerelease", "1.2.3-alpha.1.beta", "alpha"},
            {"1.2.3-alpha.10.0.beta", "prerelease", "1.2.3-dev.0", "dev"},
            {"1.2.3-alpha.10.0.beta", "prerelease", "1.2.3-alpha.10.1.beta", "alpha"},
            {"1.2.3-alpha.10.1.beta", "prerelease", "1.2.3-alpha.10.2.beta", "alpha"},
            {"1.2.3-alpha.10.2.beta", "prerelease", "1.2.3-alpha.10.3.beta", "alpha"},
            {"1.2.3-alpha.10.beta.0", "prerelease", "1.2.3-dev.0", "dev"},
            {"1.2.3-alpha.10.beta.0", "prerelease", "1.2.3-alpha.10.beta.1", "alpha"},
            {"1.2.3-alpha.10.beta.1", "prerelease", "1.2.3-alpha.10.beta.2", "alpha"},
            {"1.2.3-alpha.10.beta.2", "prerelease", "1.2.3-alpha.10.beta.3", "alpha"},
            {"1.2.3-alpha.9.beta", "prerelease", "1.2.3-dev.0", "dev"},
            {"1.2.3-alpha.9.beta", "prerelease", "1.2.3-alpha.10.beta", "alpha"},
            {"1.2.3-alpha.10.beta", "prerelease", "1.2.3-alpha.11.beta", "alpha"},
            {"1.2.3-alpha.11.beta", "prerelease", "1.2.3-alpha.12.beta", "alpha"},
            {"1.2.0", "prepatch", "1.2.1-dev.0", "dev"},
            {"1.2.0-1", "prepatch", "1.2.1-dev.0", "dev"},
            {"1.2.0", "preminor", "1.3.0-dev.0", "dev"},
            {"1.2.3-1", "preminor", "1.3.0-dev.0", "dev"},
            {"1.2.0", "premajor", "2.0.0-dev.0", "dev"},
            {"1.2.3-1", "premajor", "2.0.0-dev.0", "dev"},
            {"1.2.0-1", "minor", "1.2.0", "dev"},
            {"1.0.0-1", "major", "1.0.0", "dev"},
            {"1.2.3-dev.bar", "prerelease", "1.2.3-dev.0", "dev"},      
    };
}
