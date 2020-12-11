/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ComponentType {
    @JsonProperty("aws.greengrass.generic")
    GENERIC,
    @JsonProperty("aws.greengrass.lambda")
    LAMBDA,
    @JsonProperty("aws.greengrass.plugin")
    PLUGIN,
    @JsonProperty("aws.greengrass.nucleus")
    NUCLEUS;
}
