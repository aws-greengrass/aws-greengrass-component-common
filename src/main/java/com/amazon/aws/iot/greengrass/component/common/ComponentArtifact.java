package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.net.URI;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = ComponentArtifact.ComponentArtifactBuilder.class)
@Value
@Builder
public class ComponentArtifact {
    URI uri;

    String digest;

    String algorithm;

    String unarchive;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentArtifactBuilder {
    }
}
