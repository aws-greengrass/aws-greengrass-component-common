/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0 */

package com.amazon.aws.iot.greengrass.configuration.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonSerialize
@JsonDeserialize(builder = ConfigurationValidationPolicy.ConfigurationValidationPolicyBuilder.class)
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationValidationPolicy {
    private Integer timeout;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ConfigurationValidationPolicyBuilder {
    }
}
