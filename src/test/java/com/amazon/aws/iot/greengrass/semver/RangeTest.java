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

import static com.amazon.aws.iot.greengrass.semver.RangeExclude.RANGE_EXCLUDE;
import static com.amazon.aws.iot.greengrass.semver.RangeInclude.RANGE_INCLUDE;
import static com.amazon.aws.iot.greengrass.semver.RangeIntersects.RANGE_INTERSECTS;
import static com.amazon.aws.iot.greengrass.semver.RangeParse.RANGE_PARSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeTest {
    @Test
    void range_tests() {
        for (String[] r : RANGE_INCLUDE) {
            Range range;
            try {
                range = new Range(r[0]);
            } catch (IllegalArgumentException e) {
                System.out.println(r[0]);
                throw e;
            }
            assertTrue(range.test(new SemVer(r[1])), String.format("range %s is satisfied by %s", r[0], r[1]));
        }
    }

    @Test
    void range_parsing() {
        for (String[] rp : RANGE_PARSE) {
            String range = rp[0];
            String expect = rp[1];

            if (expect == null) {
                assertEquals("Invalid comparator " + range,
                        assertThrows(IllegalArgumentException.class,
                        () -> new Range(range)).getMessage());
            } else {
                String r = new Range(range).range;
                if (r == null || r.isEmpty()) {
                    r = "*";
                }
                assertEquals(expect, r, String.format("Expected %s to be %s", range, expect));
                assertEquals(new Range(expect).range, new Range(range).range);
            }
        }
    }

    @Test
    void negative_range_tests() {
        for (String[] r: RANGE_EXCLUDE) {
            Range range = new Range(r[0]);

            try {
                SemVer ver = new SemVer(r[1]);
                assertFalse(range.test(ver), String.format("%s not satisfied by %s", r[0], r[1]));
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Test
    void tostrings() {
        assertEquals(new Range(">= v1.2.3").range, ">=1.2.3");
    }

    @Test
    void ranges_intersect() {
        for (String[] r : RANGE_INTERSECTS) {
            String r0 = r[0];
            String r1 = r[1];
            boolean expect = Boolean.parseBoolean(r[2]);

            Range range0 = new Range(r0);
            Range range1 = new Range(r1);

            assertEquals(expect, range0.intersects(range1),
                    String.format("%s <~> %s objects", r0, r1));
            assertEquals(expect, range1.intersects(range0),
                    String.format("%s <~> %s objects", r1, r0));
        }
    }
}
