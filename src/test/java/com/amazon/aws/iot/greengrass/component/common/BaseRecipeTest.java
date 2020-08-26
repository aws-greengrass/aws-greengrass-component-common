package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseRecipeTest {

    static final ObjectMapper DESERIALIZER_YAML = SerializerFactory.getRecipeSerializer();
    static final ObjectMapper DESERIALIZER_JSON = SerializerFactory.getRecipeSerializerJson();

    static Path SAMPLE_RECIPES_PATH;

    static {
        try {
            SAMPLE_RECIPES_PATH = Paths.get(ComponentRecipeDeserializationTest.class.getClassLoader()
                    .getResource("recipes")
                    .toURI());
        } catch (URISyntaxException ignore) {
        }
    }
}
