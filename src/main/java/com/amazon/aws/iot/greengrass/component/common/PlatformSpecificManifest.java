/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    List<ComponentParameter> parameters = Collections.emptyList();

    @Deprecated // Plan to remove for re:Invent
    @Builder.Default
    Map<String, Object> lifecycle = Collections.emptyMap();

    @Builder.Default
    List<ComponentArtifact> artifacts = Collections.emptyList();

    @Builder.Default
    Map<String, DependencyProperties> dependencies = Collections.emptyMap();

    /**
     * Set of lifecycle selections enabled by this platform (optional)
     */
    @Builder.Default
    List<String> selections = null;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PlatformSpecificManifestBuilder {
    }

}
