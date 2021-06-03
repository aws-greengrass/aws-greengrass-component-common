/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>Class representing a Platform map. A platform map is a set of key/value pairs for matching against arbitrary keys.
 * Some well defined keys do exist, but is irrelevant for Manifest data model.</p>
 * <p>The value is a match expression, as follows:
 *     <ul>
 *         <li>name=stringValue - where stringValue beings with letter or digit - perform an exact match.</li>
 *         <li>name=/regex/ - match string against regular expression string.</li>
 *         <li>name="*" - match string against anything, including missing value.</li>
 *     </ul>
 */
public class Platform extends HashMap<String,String> {
    // This is really a map
    // Existing code assumes a Platform class with some methods, so this helps smooth the code migration process
    //@Deprecated
    public static final String ALL_KEYWORD = "all";
    // Platform with no key/value pairs
    public static final Platform EMPTY = new Platform();
    public static final String OS_KEY = "os";
    public static final String ARCHITECTURE_KEY = "architecture";
    public static final String WILDCARD = "*";

    /**
     * Retrieve specified field. Use wildcard if field does not exist or empty string.
     * @param name Name of field
     * @return Field, substituting wildcard as needed.
     */
    public String getFieldOrWild(String name) {
        Object o = get(name);
        if (o == null || ((String)o).length() == 0) {
            return WILDCARD;
        } else {
            return (String)o;
        }
    }

    //@Deprecated
    private <T extends Enum<T>> T getEnum(String name, Function<String, T> transform) {
        return transform.apply(getFieldOrWild(name));
    }

    // This is transitional
    //@Deprecated
    public OS getOs() {
        return getEnum(OS_KEY, OS::getOS);
    }

    // This is transitional
    //@Deprecated
    public Architecture getArchitecture() {
        return getEnum(ARCHITECTURE_KEY, Architecture::getArch);
    }

    /**
     * Retrieve OS, or Wildcard if OS not specified
     * @return OS as a string with expected default.
     */
    public String getOsField() {
        return getFieldOrWild(OS_KEY);
    }

    /**
     * Retrieve Architecture, or Wildcard if Architecture not specified
     * @return Architecture as a string with expected default.
     */
    public String getArchitectureField() {
        return getFieldOrWild(ARCHITECTURE_KEY);
    }

    /**
     * Backward compatibility only for transition: Set of OSes.
     */
    @Getter
    @AllArgsConstructor
    //@Deprecated
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
            if (value == null || "any".equalsIgnoreCase(value) || "all".equalsIgnoreCase(value) || "*".equals(value)) {
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
    @Getter
    @AllArgsConstructor
    //@Deprecated
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
            if (value == null || "any".equalsIgnoreCase(value) || "all".equalsIgnoreCase(value) || "*".equals(value)) {
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

    /**
     * This is to help migration to new Platform class
     */
    //@Deprecated
    public static final class PlatformBuilder {
        private final Map<String, String> platform = new HashMap<String,String>();

        public PlatformBuilder os(OS value) {
            if (value == OS.ALL) {
                return add(OS_KEY, "*");
            } else {
                return add(OS_KEY, value.name);
            }
        }

        public PlatformBuilder architecture(Architecture value) {
            if (value == Architecture.ALL) {
                return add(ARCHITECTURE_KEY, "*");
            } else {
                return add(ARCHITECTURE_KEY, value.name);
            }
        }

        public PlatformBuilder add(String name, String value) {
            if (value != null) {
                platform.put(name, value);
            }
            return this;
        }

        public Platform build() {
            Platform p = new Platform();
            p.putAll(this.platform);
            return p;
        }
    }

    public static PlatformBuilder builder() {
        return new PlatformBuilder();
    }
}
