/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.configuration.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonDeserialize(builder = Configuration.ConfigurationBuilder.class)
public class Configuration {

    public static final FailureHandlingPolicy DEFAULT_FAILURE_HANDLING_POLICY = FailureHandlingPolicy.ROLLBACK;

    public static final SchemaDate DEFAULT_SCHEMA_DATE = SchemaDate.MAY_17_2021;

    private String deploymentId;

    @NonNull
    @Builder.Default
    private SchemaDate schemaDate = DEFAULT_SCHEMA_DATE;

    private String deploymentName;

    private String configurationArn;

    private String parentTargetArn;

    private String onBehalfOf;

    private Long creationTimestamp;

    @NonNull
    @Builder.Default
    private Map<String, ComponentUpdate> components = new HashMap<>();

    @NonNull
    @Builder.Default
    private FailureHandlingPolicy failureHandlingPolicy = DEFAULT_FAILURE_HANDLING_POLICY;

    @NonNull
    @Builder.Default
    // Enum can't be used because values are expected to be expanded and the field should always be de-serializable
    private List<String> requiredCapabilities = new ArrayList<>();

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