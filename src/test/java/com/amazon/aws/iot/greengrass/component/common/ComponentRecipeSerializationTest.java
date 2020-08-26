package com.amazon.aws.iot.greengrass.component.common;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;

public class ComponentRecipeSerializationTest extends BaseRecipeTest {

    @Test
    void GIVEN_a_recipe_file_yaml_WHEN_we_deserialize_it_and_serialize_it_back_THEN_we_get_the_same_thing() throws IOException {
        String filename = "sample-recipe-with-all-fields.yaml";
        Path recipePath = SAMPLE_RECIPES_PATH.resolve(filename);

        String recipeString = new String(Files.readAllBytes(recipePath));
        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(recipeString,
                ComponentRecipe.class);

        assertThat(DESERIALIZER_YAML.readValue(DESERIALIZER_YAML.writeValueAsString(recipe), ComponentRecipe.class), Is.is(recipe));
    }

    @Test
    void GIVEN_a_recipe_file_json_WHEN_we_deserialize_it_and_serialize_it_back_THEN_we_get_the_same_thing() throws IOException {
        String filename = "sample-recipe-with-all-fields.json";
        Path recipePath = SAMPLE_RECIPES_PATH.resolve(filename);

        String recipeString = new String(Files.readAllBytes(recipePath));
        ComponentRecipe recipe = DESERIALIZER_JSON.readValue(recipeString,
                ComponentRecipe.class);

        assertThat(DESERIALIZER_JSON.readValue(DESERIALIZER_JSON.writeValueAsString(recipe), ComponentRecipe.class), Is.is(recipe));
    }
}
