package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@JsonDeserialize(builder = Platform.PlatformBuilder.class)
@Builder
@Value
public class Platform {
    public static final String ALL_KEYWORD = "all";

    @Builder.Default
    OS os = OS.ALL;

    @Builder.Default
    Architecture architecture = Architecture.ALL;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PlatformBuilder {
    }

    /**
     * Non customer-facing class. Keeps the OS hierarchy data.
     */
    @Getter
    @AllArgsConstructor
    public enum OS {
        ALL(ALL_KEYWORD),
        WINDOWS("windows"),
        LINUX("linux"),
        DARWIN("darwin"),

        @Deprecated
        // deprecated. Keep this only for console test purpose.
        MACOS("macos"),

        UNKNOWN("unknown");

        @JsonValue
        private final String name;

        /**
         * get OS enum from string value. Ignore case.
         * Unrecognized values will map to OS.ALL
         * @param value String of OS
         * @return OS enum
         */
        @JsonCreator
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
     * Non customer-facing class. Currently only has name field.
     */
    @Getter
    @AllArgsConstructor
    public enum Architecture {
        ALL(ALL_KEYWORD),
        AMD64("amd64"),
        ARM("arm"),
        AARCH64("aarch64"),
        X86("x86"),
        UNKNOWN("unknown");

        @JsonValue
        private final String name;

        /**
         * get Architecture enum from string value. Ignore case.
         * Unrecognized values will map to Architecture.ALL
         * @param value String of Architecture
         * @return Architecture enum
         */
        @JsonCreator
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
