/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;


import com.amazon.aws.iot.greengrass.semver.Range;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonDeserialize(builder = DependencyProperties.DependencyPropertiesBuilder.class)
@Value
public class DependencyProperties {
    @JsonSerialize(using = RequirementSerializer.class)
    @NonNull
    Range versionRequirement;

    DependencyType dependencyType;

    @Builder
    public DependencyProperties(@NonNull String versionRequirement, DependencyType dependencyType) {
        this.versionRequirement = new Range(versionRequirement);
        this.dependencyType = dependencyType == null ? DependencyType.HARD : dependencyType;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class DependencyPropertiesBuilder {
    }
}
