/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0 */

package com.amazon.aws.iot.greengrass.component.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.function.Function;

/**
 * <p>Class representing a Platform map. A platform map is a set of key/value pairs for matching against arbitrary keys.
 * Some well defined keys do exist, but is irrelevant for Manifest data model.</p>
 * <p>The value is a match expression, as follows:
 *     <ul>
 *         <li>name=stringValue - where stringValue beings with letter or digit - perform an exact match.</li>
 *         <li>name=/regex/ - match string against regular expression.</li>
 *         <li>name=[v1, v2, v3, ...] - provide alternatives (stringValue, regex, etc).</li>
 *         <li>name=// - match string against anything, but must have a value ("must exist").</li>
 *         <li>name=[] - match string against anything, but string need not have a value.</li>
 *     </ul>
 * </p>
 */
public class Platform extends HashMap<String,Object> {
    // This is really a map
    // Existing code assumes a Platform class with some methods, so this helps smooth the code migration process
    @Deprecated
    public static final String ALL_KEYWORD = "all";
    // Platform with no key/value pairs
    public static final Platform EMPTY = new Platform();

    @Deprecated
    private <T extends Enum<T>> T getEnum(String name, Function<String, T> transform) {
        Object o = get(name);
        if (o instanceof String) {
            return transform.apply((String)o);
        } else {
            return transform.apply(null);
        }
    }

    // This is transitional
    @Deprecated
    public OS getOs() {
        return getEnum("os", OS::getOS);
    }

    // This is transitional
    @Deprecated
    public Architecture getArchitecture() {
        return getEnum("architecture", Architecture::getArch);
    }

    /**
     * Backward compatibility only for transition: Set of OSes.
     */
    @Getter
    @AllArgsConstructor
    @Deprecated
    public enum OS {
        ALL(ALL_KEYWORD),
        WINDOWS("windows"),
        LINUX("linux"),
        DARWIN("darwin"),
        MACOS("macos"),
        UNKNOWN("unknown");

        private final String name;

        /**
         * Backward compatibility only for transition: Convert string to enum value.
         * @param value String value to convert
         * @return enum value
         */
        public static OS getOS(String value) {
            // "any" and "all" keyword are both accepted in recipe.
            if (value == null || "any".equalsIgnoreCase(value) || "all".equalsIgnoreCase(value)) {
                return OS.ALL;
            }

            for (OS os : values()) {
                if (os.getName().equals(value)) {
                    return os;
                }
            }

            // return UNKNOWN instead of throw exception. This is to keep backwards compatibility when
            // cloud recipe has more supported platform than local.
            return OS.UNKNOWN;
        }
    }

    /**
     * Backward compatibility only for transition: Set of Architectures.
     */
    @Deprecated
    @Getter
    @AllArgsConstructor
    public enum Architecture {
        ALL(ALL_KEYWORD),
        AMD64("amd64"),
        ARM("arm"),
        AARCH64("aarch64"),
        X86("x86"),
        UNKNOWN("unknown");

        private final String name;

        /**
         * Backward compatibility only for transition: Convert string to enum value.
         * @param value String value to convert
         * @return enum value
         */
        public static Architecture getArch(String value) {
            if (value == null || "any".equalsIgnoreCase(value) || "all".equalsIgnoreCase(value)) {
                // "any" and "all" keyword are both accepted in recipe.
                return Architecture.ALL;
            }

            for (Architecture arch : values()) {
                if (arch.getName().equals(value)) {
                    return arch;
                }
            }
            return Architecture.UNKNOWN;
        }
    }
}
