/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

class RecipeSchemaTest extends BaseRecipeTest {

    static JsonSchema jsonRecipeSchemaValidator;
    static JsonSchema yamlRecipeSchemaValidator;

    static ObjectMapper jsonObjectMapper = SerializerFactory.getRecipeSerializerJson();
    static ObjectMapper yamlObjectMapper = SerializerFactory.getRecipeSerializer();

    @BeforeAll
    static void setupRecipeSchemaValidator() throws URISyntaxException, IOException {
        Path recipePath = Paths.get(ComponentRecipe.class.getClassLoader()
                .getResource("recipe").toURI()).resolve("recipe_schema.json");


        String schemaString = new String(Files.readAllBytes(recipePath));

        JsonSchemaFactory jsonFactory = JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .objectMapper(SerializerFactory.getRecipeSerializerJson()).build();

        JsonSchemaFactory yamlFactory = JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .objectMapper(SerializerFactory.getRecipeSerializer()).build();

        // any invalid json recipe schemas supplied will be caught here
        jsonRecipeSchemaValidator = jsonFactory.getSchema(schemaString);
        yamlRecipeSchemaValidator = yamlFactory.getSchema(schemaString);
    }


    @ParameterizedTest
    @ValueSource(strings= {
            "b1-recipe-with-all-fields.yaml",
            "sample-recipe-with-all-fields.yaml",
            "sample-recipe-missing-permission-fields.yaml",
            "wrapper-component-recipe.yaml"
    })
    void GIVEN_valid_yaml_recipe_WHEN_validate_THEN_return_success(String fileName)
            throws IOException {

        Path recipePath = getResourcePath(fileName);
        String recipeString = new String(Files.readAllBytes(recipePath));

        Set<ValidationMessage> messages = validateYamlRecipe(recipeString);
        assertThat("Recipe validated successfully", messages.isEmpty(), Is.is(true));
    }

    @ParameterizedTest
    @ValueSource(strings= {
            "b2-recipe-with-all-fields.json",
            "sample-recipe-with-all-fields.json",
            "wrapper-component-recipe.json"
    })
    void GIVEN_valid_json_recipe_WHEN_validate_THEN_return_success(String fileName)
            throws IOException {

        Path recipePath = getResourcePath(fileName);
        String recipeString = new String(Files.readAllBytes(recipePath));
        Set<ValidationMessage> messages = validateJsonRecipe(recipeString);
        assertThat("Recipe validated successfully", messages.isEmpty(), Is.is(true));
    }

    @Test
    void GIVEN_recipe_missing_required_field_WHEN_validate_THEN_return_success()
            throws IOException {
        Path recipePath = getResourcePath("sample-recipe-fail-missing-uri.yaml");
        String recipeString = new String(Files.readAllBytes(recipePath));
        Set<ValidationMessage> messages = validateYamlRecipe(recipeString);
        assertThat("Missing required URI caught", messages.toString().contains("uri: is missing"), Is.is(true));
    }

    @Test
    void GIVEN_recipe_with_long_component_name_WHEN_validate_THEN_return_success()
            throws IOException {
        Path recipePath = getResourcePath("sample-recipe-with-long-component-version.yaml");
        String recipeString = new String(Files.readAllBytes(recipePath));
        Set<ValidationMessage> messages = validateYamlRecipe(recipeString);
        assertThat("Missing required URI caught", messages.toString().contains("may only be 64 char"), Is.is(true));
    }

    private Set<ValidationMessage> validateYamlRecipe(String recipe) throws JsonProcessingException {
        JsonNode recipeNode = yamlObjectMapper.readTree(recipe);
        recipeNode = lowerJsonNodeKeys(recipeNode, yamlObjectMapper);
        Set<ValidationMessage> messages = yamlRecipeSchemaValidator.validate(recipeNode);
        if (!messages.isEmpty()) {
            System.out.println(messages);
        };
        return messages;
    }

    private Set<ValidationMessage> validateJsonRecipe(String recipe) throws JsonProcessingException {
        JsonNode recipeNode = jsonObjectMapper.readTree(recipe);
        recipeNode = lowerJsonNodeKeys(recipeNode, jsonObjectMapper);
        Set<ValidationMessage> messages = jsonRecipeSchemaValidator.validate(recipeNode);
        if (!messages.isEmpty()) {
            System.out.println(messages);
        };
        return messages;
    }

    /*
    Perform basic preprocessing to lower all recipe keys, does not perform complete preprocessing which involves
    converting recipe to ComponentRecipe object to normalize enums. We do not perform this here as this cannot be
    enforced by json schema alone.
     */
    private static JsonNode lowerJsonNodeKeys(JsonNode node, ObjectMapper mapper) {
        ObjectNode loweredNode = mapper.createObjectNode();
        Iterator<String> fieldNames = node.fieldNames();

        // return value if iterating over json primitives
        if (!fieldNames.hasNext()) {
            return node;
        }

        while (fieldNames.hasNext()) {
            String currentName = fieldNames.next();
            JsonNode currentValue = null;
            if (node.get(currentName).isObject()) {
                // checking for nestedNode in currentNode
                JsonNode nestedNode = node.get(currentName);
                currentValue = lowerJsonNodeKeys(nestedNode, mapper);
            } else if (node.get(currentName).isArray()) {
                ArrayNode arrayNode = mapper.createArrayNode();

                for (int i = 0; i < node.get(currentName).size(); i++) {
                    arrayNode.add(lowerJsonNodeKeys(node.get(currentName).get(i), mapper));
                }
                currentValue = arrayNode;
            } else {
                currentValue = node.get(currentName);
            }
            loweredNode.put(currentName.toLowerCase(Locale.ROOT), currentValue);
        }
        return loweredNode;
    }
}
