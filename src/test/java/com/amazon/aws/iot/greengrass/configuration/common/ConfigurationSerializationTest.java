/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0 */

package com.amazon.aws.iot.greengrass.configuration.common;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;

class ConfigurationSerializationTest extends BaseConfigurationTest {

    @Test
    void GIVEN_deploy_1_component_replace_WHEN_we_deserialize_it_and_serialize_it_back_THEN_we_get_the_same_thing()
            throws IOException {
        String filename = "configuration-1-component-replace.json";
        Path configurationPath = getResourcePath(filename);

        Configuration original = DESERIALIZER_JSON.readValue(
                new String(Files.readAllBytes(configurationPath)), Configuration.class);

        Configuration copy = DESERIALIZER_JSON.readValue(
                DESERIALIZER_JSON.writeValueAsString(original), Configuration.class);

        assertThat(copy, Is.is(original));
    }
}
