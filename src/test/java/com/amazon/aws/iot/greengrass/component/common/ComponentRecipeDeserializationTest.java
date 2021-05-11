/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentRecipeDeserializationTest extends BaseRecipeTest {

    @Test
    void GIVEN_recipe_with_all_possible_fields_yaml_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "sample-recipe-with-all-fields.yaml";
        Path recipePath = getResourcePath(filename);

        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyRecipeWithAllFields(recipe);
    }

    @Test
    void GIVEN_recipe_with_missing_required_field_uri_yaml_WHEN_attempt_to_deserialize_THEN_throws_exception() {
        String filename = "sample-recipe-fail-missing-uri.yaml";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("uri is marked non-null but is null"));
    }

    @Test
    void GIVEN_recipe_with_missing_required_field_versionRequirement_yaml_WHEN_attempt_to_deserialize_THEN_throws_exception() {
        String filename = "sample-recipe-fail-missing-versionRequirement.yaml";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                        ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("versionRequirement is marked non-null but is null"));
    }

    @Test
    void GIVEN_recipe_with_all_possible_fields_json_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "sample-recipe-with-all-fields.json";
        Path recipePath = getResourcePath(filename);

        ComponentRecipe recipe = DESERIALIZER_JSON.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyRecipeWithAllFields(recipe);
    }

    @Test
    void GIVEN_recipe_with_missing_permission_fields_yaml_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "sample-recipe-missing-permission-fields.yaml";
        Path recipePath = getResourcePath(filename);

        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        assertThat(recipe.getManifests(), Matchers.not(Matchers.empty()));

        // only checking permissions here
        recipe.getManifests().forEach(m -> {
            assertThat(m.getArtifacts(), Matchers.not(Matchers.empty()));
            m.getArtifacts().forEach(a -> {
                assertThat(a.getPermission().getRead(), Is.is(PermissionType.OWNER));
                assertThat(a.getPermission().getExecute(), Is.is(PermissionType.NONE));
            });
        });
    }

    @Test
    void GIVEN_recipe_with_illegal_component_name_yaml_WHEN_attempt_to_deserialize_THEN_throws_exception() {
        String filename = "sample-recipe-with-illegal-component-name.yaml";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                        ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("Component name could only include characters of"));
    }

    @Test
    void GIVEN_recipe_with_long_component_version_yaml_WHEN_attempt_to_deserialize_THEN_throws_exception() {
        String filename = "sample-recipe-with-long-component-version.yaml";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                        ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("Component version length exceeds"));
    }

    void verifyRecipeWithAllFields(final ComponentRecipe recipe) {
        assertThat(recipe.getComponentName(), Is.is("FooService"));
        assertThat(recipe.getComponentVersion()
                .getValue(), Is.is("1.0.0"));
        assertThat(recipe.getComponentType(), Is.is(ComponentType.PLUGIN));

        assertThat(recipe.getComponentDependencies(), IsMapContaining.hasEntry("BarService",
                new DependencyProperties.DependencyPropertiesBuilder().versionRequirement("^1.1")
                        .dependencyType(DependencyType.SOFT)
                        .build()));
        assertThat(recipe.getComponentDependencies(), IsMapContaining.hasEntry("BazService",
                new DependencyProperties.DependencyPropertiesBuilder().versionRequirement("^2.0")
                        .build()));

        assertThat(recipe.getManifests()
                .size(), Is.is(2));
        PlatformSpecificManifest manifest = recipe.getManifests()
                .get(0);
        assertEquals("windows", manifest.getPlatform().get("os"));
        assertEquals("amd64", manifest.getPlatform().get("architecture"));
        assertEquals("/list|of|options/", manifest.getPlatform().get("random"));
        assertThat(manifest.getLifecycle()
                .size(), Is.is(0)); // deprecated
        assertThat(manifest.getSelections(),
                contains(
                        equalTo("one"),
                        equalTo("two"),
                        equalTo("three"),
                        equalTo("four")));
        assertThat(manifest.getName(), equalTo("Platform 1"));

        assertThat(manifest.getArtifacts()
                .size(), Is.is(2));
        ComponentArtifact artifact = manifest.getArtifacts()
                .get(0);
        assertThat(artifact.getUri()
                .toString(), Is.is("s3://some-bucket/hello_world.py"));
        assertThat(artifact.getDigest(), Is.is("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f"));
        assertThat(artifact.getAlgorithm(), Is.is("SHA-256"));
        assertThat(artifact.getUnarchive(), Is.is(Unarchive.ZIP));
        assertThat(artifact.getPermission().getRead(), Is.is(PermissionType.ALL));
        assertThat(artifact.getPermission().getExecute(), Is.is(PermissionType.ALL));

        manifest = recipe.getManifests()
                .get(1);
        assertThat(manifest.getPlatform(), IsNull.nullValue());
        assertThat(manifest.getLifecycle()
                .size(), Is.is(0)); // deprecated
        assertThat(manifest.getSelections(), IsNull.nullValue());
        assertThat(manifest.getName(), IsNull.nullValue());
        assertThat(manifest.getArtifacts()
                .size(), Is.is(1));
        artifact = manifest.getArtifacts()
                .get(0);
        assertThat(artifact.getUri()
                .toString(), Is.is("s3://some-bucket/hello_world.py"));
        assertThat(artifact.getDigest(), Is.is("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f"));
        assertThat(artifact.getAlgorithm(), Is.is("SHA-256"));
        assertThat(artifact.getPermission().getRead(), Is.is(PermissionType.ALL));
        assertThat(artifact.getPermission().getExecute(), Is.is(PermissionType.ALL));

        // Lifecycle has now moved back to recipe. It has a very fluid syntax
        assertThat(recipe.getLifecycle(), allOf(
                aMapWithSize(3),
                hasKey(equalTo("one")),
                hasKey(equalTo("two")),
                hasKey(equalTo("any"))));
        assertThat(recipe.getLifecycle().get("any"), instanceOf(Map.class));
        assertThat((Map<String,Object>)recipe.getLifecycle().get("any"), allOf(
                aMapWithSize(1),
                hasKey(equalTo("Install"))));

        // Accessing configuration using JsonPointer
        assertThat(recipe.getComponentConfiguration().getDefaultConfiguration().
                at("/FirstItem/message").textValue(), Is.is("hello"));

        // Accessing configuration using an object mapper
        try {
            ObjectMapper objectMapper =
                    new ObjectMapper(new JsonFactory())
                            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                            .findAndRegisterModules();
            _Configuration modeledConfig = objectMapper.treeToValue(recipe.getComponentConfiguration().getDefaultConfiguration(), _Configuration.class);
            assertThat(modeledConfig.get("FirstItem").message, Is.is("hello"));
        } catch (JsonProcessingException e) {
            Assertions.fail(e);
        }
    }

    @Data
    private static class _ConfigurationItem {
        String message;
    }

    private static class _Configuration extends HashMap<String, _ConfigurationItem> {
    }

    @Test
    void GIVEN_a_wrapper_component_recipe_yaml_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "wrapper-component-recipe.yaml";
        Path recipePath = getResourcePath(filename);

        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyWrapperComponentRecipe(recipe);
    }

    @Test
    void GIVEN_a_wrapper_component_recipe_json_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "wrapper-component-recipe.json";
        Path recipePath = getResourcePath(filename);

        ComponentRecipe recipe = DESERIALIZER_JSON.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyWrapperComponentRecipe(recipe);
    }

    void verifyWrapperComponentRecipe(final ComponentRecipe recipe) {
        assertThat(recipe.getComponentName(), Is.is("PythonRuntime"));
        assertThat(recipe.getComponentVersion()
                .getValue(), Is.is("1.1.0"));
        assertThat(recipe.getManifests()
                .size(), Is.is(1));
        PlatformSpecificManifest manifest = recipe.getManifests()
                .get(0);
        assertEquals("linux", manifest.getPlatform().get("os"));
        assertEquals("amd64", manifest.getPlatform().get("architecture"));
        assertThat(manifest.getLifecycle()
                .size(), Is.is(0));
        assertThat(recipe.getLifecycle()
                .size(), Is.is(1));
        assertThat(recipe.getLifecycle(), IsMapContaining.hasEntry("Install", "apt-get install python3.7"));
    }

    @Test
    void GIVEN_a_component_recipe_json_has_null_element_in_artifacts_WHEN_deserialize_THEN_fail_with_validation_error() {
        String filename = "error-recipe-contains-null-element-in-artifacts.json";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_JSON.readValue(recipePath.toFile(), ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("Artifacts contains one or more null element(s)"));
    }

    @Test
    void GIVEN_a_component_recipe_yaml_has_null_element_in_artifacts_WHEN_deserialize_THEN_fail_with_validation_error() {
        String filename = "error-recipe-contains-null-element-in-artifacts.yaml";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_YAML.readValue(recipePath.toFile(), ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("Artifacts contains one or more null element(s)"));
    }

    @Test
    void GIVEN_a_component_recipe_json_has_null_element_in_manifests_WHEN_deserialize_THEN_fail_with_validation_error() {
        String filename = "error-recipe-contains-null-element-in-manifests.json";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_JSON.readValue(recipePath.toFile(), ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("Manifests contains one or more null element(s)"));
    }

    @Test
    void GIVEN_a_component_recipe_yaml_has_null_element_in_manifests_WHEN_deserialize_THEN_fail_with_validation_error() {
        String filename = "error-recipe-contains-null-element-in-manifests.yaml";
        Path recipePath = getResourcePath(filename);

        IOException ex = assertThrows(IOException.class,
                () -> DESERIALIZER_YAML.readValue(recipePath.toFile(), ComponentRecipe.class));
        assertThat(ex.getMessage(), containsString("Manifests contains one or more null element(s)"));
    }
}
