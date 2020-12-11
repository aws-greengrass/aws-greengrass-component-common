/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonDeserialize(builder = PlatformSpecificManifest.PlatformSpecificManifestBuilder.class)
@Value
@Builder
public class PlatformSpecificManifest {

    /**
     * Map of key/value pairs, with the following behaviors:
     * <ul>
     *     <li>Name=String - exact match as long as string begins with letter or digit
     *             String must begin with [-A-Za-z0-9_$] though set may be extended</li>
     *     <li>Name=/String/ - regular expression match (not yet implemented)</li>
     *     <li>Name=[Spec1, Spec2, Spec3] - alternative matches</li>
     * </ul>
     * A String beginning with any other symbol is reserved for future use.
     */
    @Builder.Default
    Platform platform = null;

    /**
     * Friendly name of this platform. If missing, UI name is derived from set of attributes in Platform
     */
    @Builder.Default
    String name = null;

    @Builder.Default
    Map<String, Object> lifecycle = Collections.emptyMap();

    List<ComponentArtifact> artifacts;

    /**
     * Set of lifecycle selections enabled by this platform (optional)
     */
    @Builder.Default
    List<String> selections = null;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PlatformSpecificManifestBuilder {
        private List<ComponentArtifact> artifacts = Collections.emptyList(); // default to empty list

        public PlatformSpecificManifestBuilder artifacts(List<ComponentArtifact> artifacts) {
            // override lombok generated builder for custom validation and processing

            if (artifacts == null) {
                // allow null artifacts to be friendly
                // treat it as empty list for safer processing since not required to
                // differentiate null vs empty list for artifacts
                this.artifacts = Collections.emptyList();
                return this;
            }

            if (artifacts.stream().anyMatch(Objects::isNull)) {
                // doesn't allow list contain any null elements
                throw new IllegalArgumentException("Artifacts contains one or more null element(s)");
            }

            this.artifacts = artifacts;
            return this;
        }
    }
}
