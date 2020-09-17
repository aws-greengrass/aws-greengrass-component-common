package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdurmont.semver4j.Semver;
import java.util.Map;
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
    RecipeFormatVersion recipeFormatVersion;

    @NonNull
    String componentName;

    @NonNull
    @JsonSerialize(using = SemverSerializer.class)
    Semver componentVersion;

    @Builder.Default
    ComponentType componentType = ComponentType.GENERIC;

    String componentDescription;

    String componentPublisher;

    String componentSource;

    @Builder.Default
    Map<String, DependencyProperties> componentDependencies = Collections.emptyMap();

    @Builder.Default
    List<PlatformSpecificManifest> manifests = Collections.emptyList();

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentRecipeBuilder {
    }

}
