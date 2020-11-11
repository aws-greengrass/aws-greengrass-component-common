package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Permission settings for component artifacts. Read and Execute permissions can be set.
 * By default, the read permission is set to allow only the owner of the artifact on the disk and the execute permission
 * is set to all no execution.
 */
@Value
@Builder
@JsonDeserialize(builder = Permission.PermissionBuilder.class)
public class Permission {
    @NonNull
    @Builder.Default
    PermissionType read = PermissionType.OWNER;
    @NonNull
    @Builder.Default
    PermissionType execute = PermissionType.NONE;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PermissionBuilder {

    }
}
