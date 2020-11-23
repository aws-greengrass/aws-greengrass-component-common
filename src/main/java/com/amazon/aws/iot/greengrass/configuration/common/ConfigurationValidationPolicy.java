package com.amazon.aws.iot.greengrass.configuration.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@JsonSerialize
@JsonDeserialize(builder = ConfigurationValidationPolicy.ConfigurationValidationPolicyBuilder.class)
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationValidationPolicy {

    public static final Integer DEFAULT_TIMEOUT = 60;

    @NonNull
    @Builder.Default
    private Integer timeout = DEFAULT_TIMEOUT;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ConfigurationValidationPolicyBuilder {
    }
}
