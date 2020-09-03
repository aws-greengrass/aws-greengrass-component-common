package com.amazon.aws.iot.greengrass.component.common;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdurmont.semver4j.Requirement;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonDeserialize(builder = DependencyProperties.DependencyPropertiesBuilder.class)
@Value
public class DependencyProperties {
    @JsonSerialize(using = RequirementSerializer.class)
    @NonNull
    Requirement versionRequirement;

    String dependencyType;

    @Builder
    public DependencyProperties(@NonNull String versionRequirement, String dependencyType) {
        this.versionRequirement = Requirement.buildNPM(versionRequirement);
        this.dependencyType = dependencyType == null ? "HARD": dependencyType;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class DependencyPropertiesBuilder {
    }
}
