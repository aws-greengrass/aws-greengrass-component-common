package com.amazon.aws.iot.greengrass.component.common;

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
    Map<String,Object> platform = Collections.emptyMap();

    @Builder.Default
    List<ComponentParameter> parameters = Collections.emptyList();

    @Deprecated // Remove for re:Invent
    @Builder.Default
    Map<String, Object> lifecycle = Collections.emptyMap();

    @Builder.Default
    List<ComponentArtifact> artifacts = Collections.emptyList();

    @Builder.Default
    Map<String, DependencyProperties> dependencies = Collections.emptyMap();

    /**
     * Set of lifecycle selections enabled by this platform
     */
    @Builder.Default
    List<String> selections = Collections.emptyList();

    @JsonPOJOBuilder(withPrefix = "")
    public static class PlatformSpecificManifestBuilder {
    }

}
