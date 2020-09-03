package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdurmont.semver4j.Semver;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@JsonDeserialize(builder = ComponentRecipe.ComponentRecipeBuilder.class)
@Value
@Builder
public class ComponentRecipe {
    @NonNull
    RecipeTemplateVersion templateVersion;

    @NonNull
    String componentName;

    @NonNull
    @JsonSerialize(using = SemverSerializer.class)
    Semver version;

    ComponentType componentType;

    String description;

    String publisher;

    String source;

    @Builder.Default
    List<PlatformSpecificManifest> manifests = Collections.emptyList();

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentRecipeBuilder {
    }

    public enum RecipeTemplateVersion {
        @JsonProperty("2020-01-25")
        JAN_25_2020
    }
}
