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

    // String osVersion;
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
        DARWIN("darwin");

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
            if ("any".equalsIgnoreCase(value)) {
                return OS.ALL;
            }

            for (OS os : values()) {
                if (os.getName().equals(value)) {
                    return os;
                }
            }
            // TODO: throw exception of unrecognized OS
            return OS.ALL;
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
        ARM("arm");

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
            if ("any".equalsIgnoreCase(value)) {
                // "any" and "all" keyword are both accepted in recipe.
                return Architecture.ALL;
            }

            for (Architecture arch : values()) {
                if (arch.getName().equals(value)) {
                    return arch;
                }
            }
            // TODO: throw exception
            return Architecture.ALL;
        }
    }
}
