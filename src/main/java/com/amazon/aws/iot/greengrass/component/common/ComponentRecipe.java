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
import java.util.Objects;

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

    ComponentConfiguration componentConfiguration;

    Map<String, DependencyProperties> componentDependencies;

    List<PlatformSpecificManifest> manifests;

    @Builder.Default
    Map<String, Object> lifecycle = Collections.emptyMap();

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentRecipeBuilder {
        private List<PlatformSpecificManifest> manifests = Collections.emptyList(); // default to empty list

        public ComponentRecipeBuilder manifests(List<PlatformSpecificManifest> manifests) {
            // override lombok generated builder for custom validation
            if (manifests == null) {
                // allow null manifests to be friendly
                // treat it as empty list for safer processing since not required to
                // differentiate null vs empty list for manifests
                this.manifests = Collections.emptyList();
                return this;
            }

            if (manifests.stream().anyMatch(Objects::isNull)) {
                // doesn't allow list contain any null element
                throw new IllegalArgumentException("Manifests contains one or more null element(s)");
            }

            this.manifests = manifests;
            return this;
        }
    }

}
