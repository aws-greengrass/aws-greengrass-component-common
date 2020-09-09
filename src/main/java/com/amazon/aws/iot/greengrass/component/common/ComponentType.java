package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ComponentType {
    @JsonProperty("aws.greengrass.generic")
    GENERIC,
    @JsonProperty("aws.greengrass.lambda")
    LAMBDA,
    @JsonProperty("aws.greengrass.plugin")
    PLUGIN;
}
