/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RecipeFormatVersion {
    @JsonProperty("2020-01-25")
    JAN_25_2020;

    public static RecipeFormatVersion latestVersion() {
        return JAN_25_2020;
    }

    /**
     * Try parse value as enum identifier, if that fails fallback to #defaultVersion
     *
     * @param value
     * @param defaultVersion
     * @return RecipeFormatVersion corresponding to value or defaultVersion
     */
    public static RecipeFormatVersion parseValueOrDefault(final String value, final RecipeFormatVersion defaultVersion) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException ex) {
            return defaultVersion;
        }
    }
}
