/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.amazon.aws.iot.greengrass.configuration.common.BaseConfigurationTest;
import com.amazon.aws.iot.greengrass.test.exception.MissingTestDataException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseRecipeTest {

    static final ObjectMapper DESERIALIZER_YAML = SerializerFactory.getRecipeSerializer();
    static final ObjectMapper DESERIALIZER_JSON = SerializerFactory.getRecipeSerializerJson();

    static String RECIPES_RESOURCE_PATH = "recipes";

    static Path getResourcePath(String filename) {
        try {
            return Paths.get(BaseConfigurationTest.class.getClassLoader()
                    .getResource(RECIPES_RESOURCE_PATH).toURI()).resolve(filename);
        } catch (Exception e) {
            throw new MissingTestDataException(RECIPES_RESOURCE_PATH, e);
        }
    }
}
