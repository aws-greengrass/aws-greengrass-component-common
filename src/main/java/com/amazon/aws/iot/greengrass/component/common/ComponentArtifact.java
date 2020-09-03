package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = ComponentArtifact.ComponentArtifactBuilder.class)
public class ComponentArtifact {
    @NonNull
    URI uri;

    String digest;

    String algorithm;

    Unarchive unarchive;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentArtifactBuilder {
    }
}
