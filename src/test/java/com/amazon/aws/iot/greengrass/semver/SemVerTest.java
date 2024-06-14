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


import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SemVerTest {

    @Test
    void stringification() {
        String in = "1.1.1-pre+build.end-build123";
        assertEquals(in, new SemVer(in).getValue());
    }

    @Test
    void comparisons() {
        for (String[] comp : Comparisons.COMPARISONS) {
            String v0 = comp[0];
            String v1 = comp[1];
            SemVer s0 = new SemVer(v0);
            SemVer s1 = new SemVer(v1);
            assertEquals(1, s0.compareTo(s1), Arrays.toString(comp));
            assertEquals(-1, s1.compareTo(s0), Arrays.toString(comp));
            assertEquals(0, s0.compareTo(s0), Arrays.toString(comp));
            assertEquals(0, s1.compareTo(s1), Arrays.toString(comp));
        }
    }

    @Test
    void equality() {
        for (String[] comp : Equality.EQUALITY) {
            String v0 = comp[0];
            String v1 = comp[1];
            SemVer s0 = new SemVer(v0);
            SemVer s1 = new SemVer(v1);

            assertEquals(0, s0.compareTo(s1), Arrays.toString(comp));
            assertEquals(0, s1.compareTo(s0), Arrays.toString(comp));
            assertEquals(0, s0.compareTo(s0), Arrays.toString(comp));
            assertEquals(0, s1.compareTo(s1), Arrays.toString(comp));
            assertEquals(0, s0.comparePre(s1), "comparePre just to hit that code path");
        }
    }

    @Test
    void toString_equals_parsed_version() {
        assertEquals("1.2.3", new SemVer("v1.2.3").getVersion());
    }

    @Test
    void throws_when_presented_with_garbage() {
        for (String[] comp : InvalidVersions.INVALID_VERSIONS) {
            String v = comp[0];
            String msg = comp[0];
            assertThrows(IllegalArgumentException.class, () -> new SemVer(v), msg);
        }
    }

    @Test
    void invalid_version_numbers() {
        for (String v : new String[]{"1.2.3.4", "NOT VALID", null, "Infinity.NaN.Infinity" }) {
            assertEquals(String.format("Version \"%s\" is not valid", v),
                    assertThrows(IllegalArgumentException.class, () -> new SemVer(v)).getMessage());
        }
    }

    @Test
    void incrementing() {
        for (String[] v : Increments.INCREMENTS) {
            String version = v[0];
            String inc = v[1];
            String expect = v[2];
            String id = null;
            if (v.length == 4) {
                id = v[3];
            }
            if (expect == null) {
                String finalId = id;
                assertThrows(IllegalArgumentException.class, () -> new SemVer(version).inc(inc, finalId));
            } else {
                SemVer vv = new SemVer(version);
                vv = vv.inc(inc, id);
                assertEquals(expect, vv.getVersion(), Arrays.toString(v));
            }
        }
    }

    @Test
    void compare_main_vs_pre() {
        SemVer s = new SemVer("1.2.3");
        assertEquals(-1, s.compareMain(new SemVer("2.3.4")));
        assertEquals(-1, s.compareMain(new SemVer("1.2.4")));
        assertEquals(1, s.compareMain(new SemVer("0.1.2")));
        assertEquals(1, s.compareMain(new SemVer("1.2.2")));
        assertEquals(0, s.compareMain(new SemVer("1.2.3-pre")));

        SemVer p = new SemVer("1.2.3-alpha.0.pr.1");
        assertEquals(0, p.comparePre(new SemVer("9.9.9-alpha.0.pr.1")));
        assertEquals(-1, p.comparePre(new SemVer("1.2.3")));
        assertEquals(-1, p.comparePre(new SemVer("1.2.3-alpha.0.pr.2")));
        assertEquals(1, p.comparePre(new SemVer("1.2.3-alpha.0.2")));
    }

    @Test
    void compareBuild() {
        SemVer noBuild = new SemVer("1.0.0");
        SemVer build0 = new SemVer("1.0.0+0");
        SemVer build1 = new SemVer("1.0.0+1");
        SemVer build10 = new SemVer("1.0.0+1.0");
        assertEquals(-1, noBuild.compareBuild(build0), String.format("%s compared to %s", noBuild.getVersion(),
                build0.getVersion()));
        assertEquals(0, build0.compareBuild(build0));
        assertEquals(1, build0.compareBuild(noBuild));

        assertEquals(-1, build0.compareBuild(new SemVer("1.0.0+0.0")));
        assertEquals(-1, build0.compareBuild(build1));
        assertEquals(1, build1.compareBuild(build0));
        assertEquals(1, build10.compareBuild(build1));
    }
}
