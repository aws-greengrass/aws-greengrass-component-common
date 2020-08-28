package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = ComponentParameter.ComponentParameterBuilder.class)
@Value
@Builder
public class ComponentParameter {

    String name;

    String value;

    ParameterType type;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentParameterBuilder {
    }

    public enum ParameterType {
        NUMBER, STRING, BOOLEAN
    }
}
