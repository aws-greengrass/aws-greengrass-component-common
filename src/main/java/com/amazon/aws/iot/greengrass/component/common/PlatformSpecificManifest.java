package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = PlatformSpecificManifest.PlatformSpecificManifestBuilder.class)
@Value
@Builder
public class PlatformSpecificManifest {

    Platform platform;

    @Builder.Default
    List<ComponentParameter> parameters = Collections.emptyList();

    @Builder.Default
    Map<String, Object> lifecycle = Collections.emptyMap();

    @Builder.Default
    List<ComponentArtifact> artifacts = Collections.emptyList();

    @Builder.Default
    Map<String, DependencyProperties> dependencies = Collections.emptyMap();

    @JsonPOJOBuilder(withPrefix = "")
    public static class PlatformSpecificManifestBuilder {
    }

}
