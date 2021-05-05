package com.amazon.aws.iot.greengrass.deployment.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = DeploymentComponentsConfiguration.DeploymentComponentsConfigurationBuilder.class)
public class DeploymentComponentsConfiguration {
    // so that we could break in the future
    SchemaVersion schemaVersion = SchemaVersion.MAY_4_2021;

    List<DeploymentComponentConfiguration> componentsConfiguration;

    private enum SchemaVersion {
        MAY_4_2021,
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class DeploymentComponentsConfigurationBuilder {
    }
}
