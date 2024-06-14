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

import static com.amazon.aws.iot.greengrass.semver.ComparatorIntersections.COMPARATOR_INTERSECTIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComparatorTest {
    @Test
    void comparator_testing() {
        Comparator c = new Comparator(">=1.2.3");
        assertTrue(c.test("1.2.4"));
        // test an invalid version, should not throw
        assertFalse(c.test("not a version string"));
    }

    @Test
    void tostrings() {
        assertEquals(">=1.2.3", new Comparator(">= v1.2.3").value);
    }

    @Test
    void intersect_comparators() {
        for (Object[] c : COMPARATOR_INTERSECTIONS) {
            String c0 = (String) c[0];
            String c1 = (String) c[1];
            boolean expect = (boolean) c[2];
            Comparator comp0 = new Comparator(c0);
            Comparator comp1 = new Comparator(c1);

            assertEquals(comp0.intersects(comp1), expect);
            assertEquals(comp1.intersects(comp0), expect);
        }
    }

    @Test
    void ANY_matches_anything() {
        Comparator c = new Comparator("");
        assertTrue(c.test("1.2.3"), "ANY matches anything");
        Comparator c1 = new Comparator(">=1.2.3");
        assertTrue(c1.test(Comparator.ANY), "anything matches ANY");
        assertTrue(c.test(Comparator.ANY), "anything matches ANY");
    }

    @Test
    void invalid_comparator_parse_throws() {
        assertEquals(assertThrows(IllegalArgumentException.class, () -> new Comparator("foo bar baz")).getMessage(),
                "Invalid comparator foo bar baz");
    }

    @Test
    void eq_is_ignored() {
        assertEquals(new Comparator("=1.2.3"), new Comparator("1.2.3"));
    }
}
