package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class SerializerFactory {
    private static final ObjectMapper YAML_RECIPE_SERIALIZER =
            new ObjectMapper(new YAMLFactory())
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private static final ObjectMapper JSON_RECIPE_SERIALIZER =
            new ObjectMapper(new JsonFactory())
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .findAndRegisterModules();

    public static ObjectMapper getRecipeSerializer() {
        return YAML_RECIPE_SERIALIZER;
    }

    public static ObjectMapper getRecipeSerializerJson() {
        return JSON_RECIPE_SERIALIZER;
    }

    private SerializerFactory() {
    }
}
