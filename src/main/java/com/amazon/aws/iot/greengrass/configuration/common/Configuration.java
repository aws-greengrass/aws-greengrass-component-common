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
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonDeserialize(builder = Configuration.ConfigurationBuilder.class)
public class Configuration {

    public static final FailureHandlingPolicy DEFAULT_FAILURE_HANDLING_POLICY = FailureHandlingPolicy.ROLLBACK;

    private String deploymentId;

    private String deploymentName;

    private String configurationArn;

    private Long creationTimestamp;

    @NonNull
    @Builder.Default
    private Map<String, ComponentUpdate> components = new HashMap<>();

    @NonNull
    @Builder.Default
    private FailureHandlingPolicy failureHandlingPolicy = DEFAULT_FAILURE_HANDLING_POLICY;

    @NonNull
    @Builder.Default
    private ComponentUpdatePolicy componentUpdatePolicy = ComponentUpdatePolicy.builder()
            .build();

    @NonNull
    @Builder.Default
    private ConfigurationValidationPolicy configurationValidationPolicy = ConfigurationValidationPolicy.builder()
            .build();

    @JsonPOJOBuilder(withPrefix = "")
    public static class ConfigurationBuilder {
    }
}
