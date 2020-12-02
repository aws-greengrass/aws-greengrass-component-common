/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vdurmont.semver4j.Semver;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ComponentRecipeSerializationTest extends BaseRecipeTest {

    @Test
    void GIVEN_the_b1_recipe_file_yaml_WHEN_we_deserialize_and_serialize_it_THEN_no_empty_fields_are_added()
            throws IOException {
        String filename = "b1-recipe-with-all-fields.yaml";
        Path recipePath = getResourcePath(filename);

        String recipeString = new String(Files.readAllBytes(recipePath));
        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(recipeString, ComponentRecipe.class);

        String resultRecipeYaml =
                DESERIALIZER_YAML.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS).writeValueAsString(recipe);

        // Not ideal, but the best we can do now. Ideally we need a b1 version schema so that we could validate.
        // The actual b1 backward compatibility test could be covered in cloud side's integration test.
        assertThat("Serialization to yaml should not generate new empty field to maintain backward compatibility",
                !resultRecipeYaml.contains("componentDependencies") && !resultRecipeYaml
                        .contains("ComponentDependencies"));

        String resultRecipeJson = DESERIALIZER_JSON.writeValueAsString(recipe);
        assertThat("Serialization to json should not generate new empty field to maintain backward compatibility",
                !resultRecipeJson.contains("componentDependencies") && !resultRecipeJson
                        .contains("ComponentDependencies"));
    }

    @Test
    void GIVEN_a_b2_recipe_file_yaml_WHEN_we_deserialize_it_THEN_all_fields_are_parsed() throws IOException {
        String filename = "b2-recipe-with-all-fields.json";
        Path recipePath = getResourcePath(filename);

        //
        // While we cannot determine B2 recipe is handled correctly here, we can at least make sure that the
        // data model can read a B2 recipe.
        //

        String recipeString = new String(Files.readAllBytes(recipePath));
        ComponentRecipe recipe = DESERIALIZER_JSON.readValue(recipeString, ComponentRecipe.class);
        assertNotNull(recipe); // effectively a schema validation pass and making sure we got a value
    }

    @Test
    void GIVEN_a_recipe_file_yaml_WHEN_we_deserialize_it_and_serialize_it_back_THEN_we_get_the_same_thing()
            throws IOException {
        String filename = "sample-recipe-with-all-fields.yaml";
        Path recipePath = getResourcePath(filename);

        String recipeString = new String(Files.readAllBytes(recipePath));
        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(recipeString, ComponentRecipe.class);

        assertThat(DESERIALIZER_YAML.readValue(DESERIALIZER_YAML.writeValueAsString(recipe), ComponentRecipe.class),
                Is.is(recipe));
    }

    @Test
    void GIVEN_a_recipe_file_json_WHEN_we_deserialize_it_and_serialize_it_back_THEN_we_get_the_same_thing()
            throws IOException {
        String filename = "sample-recipe-with-all-fields.json";
        Path recipePath = getResourcePath(filename);

        String recipeString = new String(Files.readAllBytes(recipePath));
        ComponentRecipe recipe = DESERIALIZER_JSON.readValue(recipeString, ComponentRecipe.class);

        assertThat(DESERIALIZER_JSON.readValue(DESERIALIZER_JSON.writeValueAsString(recipe), ComponentRecipe.class),
                Is.is(recipe));
    }

    @Test
    void GIVEN_a_recipe_object_WHEN_we_serialize_it_to_YAML_THEN_property_name_is_upper_camel_case()
            throws IOException {
        ComponentRecipe recipe = getDummyComponentRecipe();

        String recipeStr = DESERIALIZER_YAML.writeValueAsString(recipe);

        // ObjectMapper will fail to deserialize if a property name's case is wrong because
        // MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES is off by default, and
        // DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES is on by default.
        ComponentRecipe componentRecipe =
                new ObjectMapper(new YAMLFactory()).setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
                        .readValue(recipeStr, ComponentRecipe.class);
        assertThat(componentRecipe, Is.is(recipe));
    }

    @Test
    void GIVEN_a_recipe_object_WHEN_we_serialize_it_to_JSON_THEN_property_name_is_upper_camel_case()
            throws IOException {
        ComponentRecipe recipe = getDummyComponentRecipe();

        String recipeStr = DESERIALIZER_JSON.writeValueAsString(recipe);

        // ObjectMapper will fail to deserialize if a property name's case is wrong because
        // MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES is off by default, and
        // DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES is on by default.
        ComponentRecipe componentRecipe =
                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
                        .readValue(recipeStr, ComponentRecipe.class);
        assertThat(componentRecipe, Is.is(recipe));
    }

    private ComponentRecipe getDummyComponentRecipe() {
        return ComponentRecipe.builder().componentName("test").recipeFormatVersion(RecipeFormatVersion.JAN_25_2020)
                .componentVersion(new Semver("1.0.0")).build();
    }
}
