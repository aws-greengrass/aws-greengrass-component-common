package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = ComponentParameter.ComponentParameterBuilder.class)
@Value
@Builder
public class ComponentParameter {

    String name;

    String value;

    ParameterType type;

    public enum ParameterType {
        NUMBER, STRING, BOOLEAN
    }
}
