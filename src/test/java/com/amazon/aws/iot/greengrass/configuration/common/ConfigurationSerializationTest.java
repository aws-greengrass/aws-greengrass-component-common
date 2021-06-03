/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.configuration.common;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;

class ConfigurationSerializationTest extends BaseConfigurationTest {

    @ParameterizedTest
    @ValueSource(strings = {"configuration-1-component-replace.json", "configuration-2-component-replace.json"})
    void GIVEN_deployment_config_WHEN_deserialized_and_serialized_THEN_return_orig_config(String filename)
            throws IOException {
        Path configurationPath = getResourcePath(filename);

        Configuration original = DESERIALIZER_JSON.readValue(
                new String(Files.readAllBytes(configurationPath)), Configuration.class);

        Configuration copy = DESERIALIZER_JSON.readValue(
                DESERIALIZER_JSON.writeValueAsString(original), Configuration.class);

        assertThat(copy, Is.is(original));
    }
}
