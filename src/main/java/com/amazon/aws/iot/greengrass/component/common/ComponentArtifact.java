/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = ComponentArtifact.ComponentArtifactBuilder.class)
public class ComponentArtifact {
    @NonNull
    URI uri;

    String digest;

    String algorithm;

    @Builder.Default
    Unarchive unarchive = Unarchive.NONE;

    @Builder.Default
    Permission permission = Permission.builder().build();

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentArtifactBuilder {
    }
}
