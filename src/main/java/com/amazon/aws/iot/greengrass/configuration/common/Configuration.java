package com.amazon.aws.iot.greengrass.configuration.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonDeserialize(builder = Configuration.ConfigurationBuilder.class)
public class Configuration {

    private String deploymentId;

    private String deploymentName;

    private Long creationTimestamp;

    @NonNull
    @Builder.Default
    private Map<String, ComponentUpdate> components = Collections.emptyMap();

    private FailureHandlingPolicy failureHandlingPolicy;

    private ComponentUpdatePolicy componentUpdatePolicy;

    private ConfigurationValidationPolicy configurationValidationPolicy;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ConfigurationBuilder {
    }
}