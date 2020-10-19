package com.amazon.aws.iot.greengrass.configuration.common;

import com.amazon.aws.iot.greengrass.component.common.SerializerFactory;
import com.amazon.aws.iot.greengrass.test.exception.MissingTestDataException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseConfigurationTest {

    static final ObjectMapper DESERIALIZER_JSON = SerializerFactory.getRecipeSerializerJson();

    static String CONFIGURATION_RESOURCE_PATH = "configurations";

    static Path getResourcePath(String filename) {
        try {
            return Paths.get(BaseConfigurationTest.class.getClassLoader()
                    .getResource(CONFIGURATION_RESOURCE_PATH).toURI()).resolve(filename);
        } catch (Exception e) {
            throw new MissingTestDataException(CONFIGURATION_RESOURCE_PATH, e);
        }
    }
}
