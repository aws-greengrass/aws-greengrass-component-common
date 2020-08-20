package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.vdurmont.semver4j.Semver;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;

@JsonDeserialize(builder = ComponentRecipe.ComponentRecipeBuilder.class)
@Value
@Builder
public class ComponentRecipe {

    RecipeTemplateVersion templateVersion;

    String componentName;

    Semver version;

    String componentType;

    String description;

    String publisher;

    @Builder.Default
    @NonFinal
    List<PlatformSpecificManifest> manifests = Collections.emptyList();

    ComponentRecipe(RecipeTemplateVersion templateVersion, String componentName, Semver version, String componentType
            , String description, String publisher, List<PlatformSpecificManifest> manifests) {
        this.templateVersion = templateVersion;
        this.componentName = componentName;
        this.version = new Semver(version.getValue(), Semver.SemverType.NPM);
        this.componentType = componentType;
        this.description = description;
        this.publisher = publisher;
        this.manifests = manifests;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentRecipeBuilder {
    }

    public enum RecipeTemplateVersion {
        JAN_25_2020
    }
}
