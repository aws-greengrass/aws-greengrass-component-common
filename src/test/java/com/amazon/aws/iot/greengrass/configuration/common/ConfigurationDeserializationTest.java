/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.configuration.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ConfigurationDeserializationTest extends BaseConfigurationTest {

    private static final Long MEMORY = Long.valueOf(64);

    @Test
    void GIVEN_no_args_WHEN_new_configuration_THEN_use_defaults() {
        Configuration configuration = Configuration.builder().build();
        assertThat(configuration.getFailureHandlingPolicy(),
                Is.is(Configuration.DEFAULT_FAILURE_HANDLING_POLICY));
        assertThat(configuration.getComponentUpdatePolicy().getAction(),
                Is.is(ComponentUpdatePolicy.DEFAULT_ACTION));
        assertThat(configuration.getComponentUpdatePolicy().getTimeout(),
                Is.is(ComponentUpdatePolicy.DEFAULT_TIMEOUT));
        assertThat(configuration.getConfigurationValidationPolicy().getTimeout(),
                Is.is(ConfigurationValidationPolicy.DEFAULT_TIMEOUT));
        assertThat(configuration.getRequiredCapabilities().size(), Is.is(0));
        assertThat(configuration.getSchemaDate(), Is.is(Configuration.DEFAULT_SCHEMA_DATE));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "configuration-1-component-replace.json",
            "configuration-2-component-replace.json",
            "configuration-1-redeploy.json"
    })
    void GIVEN_configuration_1_component_replace_THEN_return_instantiated_model_instance(String filename) throws IOException {
        Path configurationPath = getResourcePath(filename);

        Configuration configuration = DESERIALIZER_JSON.readValue(
                new String(Files.readAllBytes(configurationPath)), Configuration.class);

        verifyConfiguration1ComponentReplace(configuration, filename);
    }

    void verifyConfiguration1ComponentReplace(final Configuration configuration, final String filename) throws JsonProcessingException {
        assertThat(configuration.getFailureHandlingPolicy(), Is.is(FailureHandlingPolicy.fromString("DO_NOTHING")));
        assertThat(configuration.getConfigurationValidationPolicy().getTimeout(), Is.is(30000));
        assertThat(configuration.getComponentUpdatePolicy().getTimeout(), Is.is(5000));

        assertThat(configuration.getComponentUpdatePolicy().getAction(),
                Is.is(ComponentUpdatePolicy.Action.fromString("SKIP_NOTIFY_COMPONENTS")));

        ComponentUpdate component = configuration.getComponents().get("MyThermostatComponent");
        assertThat(component, notNullValue());
        assertThat(component.getVersion().getValue(), Is.is("1.0.0"));

        RunWith runWith = component.getRunWith();
        assertThat(runWith.getPosixUser(), Is.is("user:group"));
        if (filename.equals("configuration-2-component-replace.json")) {
            assertNotNull(runWith.getSystemResourceLimits());
            assertThat(runWith.getSystemResourceLimits().getMemory(), is(MEMORY));
            assertThat(runWith.getSystemResourceLimits().getCpus(), is(51.27));
        }

        ConfigurationUpdate configurationUpdate = component.getConfigurationUpdate();
        assertThat(configurationUpdate, notNullValue());

        List<String> reset = configurationUpdate.getReset();
        assertThat(reset, hasItem("/rooms/Kitchen"));
        assertThat(reset, hasItem("/rooms/Office"));
        assertThat(reset, hasItem("/rooms/Conference Room"));

        JsonNode merge = configurationUpdate.getMerge();

        // Accessing configuration using JsonPointer
        assertThat(merge.at("/units").textValue(), Is.is("C"));

        // Accessing configuration using an object mapper
        ObjectMapper objectMapper =
                new ObjectMapper(new JsonFactory())
                        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                        .findAndRegisterModules();
        MyThermostatConfiguration modeledConfig = objectMapper.treeToValue(merge, MyThermostatConfiguration.class);
        assertThat(modeledConfig.rooms.get("Kitchen").getTemperature(), Is.is(25));
        assertThat(modeledConfig.rooms.get("Office").getTemperature(), Is.is(22));
        assertThat(modeledConfig.rooms.get("Conference Room").getTemperature(), Is.is(20));

        assertThat(configuration.getSchemaDate(), Is.is(SchemaDate.MAY_17_2021));
        assertThat(configuration.getRequiredCapabilities().size(), Is.is(0));
    }

    @Data
    private static class MyThermostatConfiguration {
        String units;
        Map<String, MyThermostatRoom> rooms = new HashMap<>();
    }

    @Data
    private static class MyThermostatRoom {
        int temperature;
        String fan;
    }
}
