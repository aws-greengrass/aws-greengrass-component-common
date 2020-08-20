package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Platform.PlatformBuilder.class)
@Builder
@Value
public class Platform {
    String os;
    String osVersion;
    String architecture;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PlatformBuilder {
    }
}
