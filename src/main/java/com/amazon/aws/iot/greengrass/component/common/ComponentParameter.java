/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonDeserialize(builder = ComponentParameter.ComponentParameterBuilder.class)
@Value
@Builder
public class ComponentParameter {

    @NonNull
    String name;

    String value;

    @NonNull
    ParameterType type;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentParameterBuilder {
    }

    public enum ParameterType {
        NUMBER, STRING, BOOLEAN
    }
}
