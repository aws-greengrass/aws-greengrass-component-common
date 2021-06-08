/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdurmont.semver4j.Semver;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public static final Pattern COMPONENT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-_.]+$");
    private static final int COMPONENT_NAME_LENGTH = 128;
    private static final int COMPONENT_MAX_VERSION_NUMBER = 999999;
    private static final int COMPONENT_VERSION_LENGTH = 64;

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

            if (manifests.stream()
                    .anyMatch(Objects::isNull)) {
                // doesn't allow list contain any null element
                throw new IllegalArgumentException("Manifests contains one or more null element(s)");
            }

            this.manifests = manifests;
            return this;
        }

        public ComponentRecipeBuilder componentName(String name) {
            if (name == null) {
                throw new NullPointerException("Component name is null");
            } else if (name.isEmpty()) {
                throw new IllegalArgumentException("Component name is empty");
            } else if (name.length() > COMPONENT_NAME_LENGTH) {
                throw new IllegalArgumentException(String.format("Component name length exceeds %d characters",
                        COMPONENT_NAME_LENGTH));
            } else {
                Matcher matcher = COMPONENT_NAME_PATTERN.matcher(name);
                if (!matcher.find()) {
                    throw new IllegalArgumentException(
                            "Component name could only include characters of ALPHA/DIGIT/\"-\"/\".\"/\"_\"");
                }
                this.componentName = name;
                return this;
            }
        }

        public ComponentRecipeBuilder componentVersion(Semver version) {
            if (version == null) {
                throw new NullPointerException("Component version is null");
            } else {
                if (version.getValue().length() > COMPONENT_VERSION_LENGTH) {
                    throw new IllegalArgumentException(String.format("Component version length exceeds %d characters",
                            COMPONENT_VERSION_LENGTH));
                }
                if (version.getMajor() > COMPONENT_MAX_VERSION_NUMBER
                        || version.getMinor() > COMPONENT_MAX_VERSION_NUMBER
                        || version.getPatch() > COMPONENT_MAX_VERSION_NUMBER) {
                    throw new IllegalArgumentException("Component version major, minor, patch can't exceed 6 digits");
                }
                this.componentVersion = version;
                return this;
            }
        }
    }

}
