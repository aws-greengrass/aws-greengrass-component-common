/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.configuration.common;

import com.amazon.aws.iot.greengrass.component.common.SemverSerializer;
import com.amazon.aws.iot.greengrass.semver.SemVer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonSerialize
@JsonDeserialize(builder = ComponentUpdate.ComponentUpdateBuilder.class)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentUpdate {

    @JsonSerialize(using = SemverSerializer.class)
    private SemVer version;

    private ConfigurationUpdate configurationUpdate;

    private RunWith runWith;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ComponentUpdateBuilder {
    }
}
