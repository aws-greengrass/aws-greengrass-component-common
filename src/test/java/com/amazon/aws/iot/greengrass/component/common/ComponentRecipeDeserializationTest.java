package com.amazon.aws.iot.greengrass.component.common;

import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

class ComponentRecipeDeserializationTest extends BaseRecipeTest {

    @Test
    void GIVEN_recipe_with_all_possible_fields_yaml_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "sample-recipe-with-all-fields.yaml";
        Path recipePath = SAMPLE_RECIPES_PATH.resolve(filename);

        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyRecipeWithAllFields(recipe);
    }

    @Test
    void GIVEN_recipe_with_all_possible_fields_json_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "sample-recipe-with-all-fields.json";
        Path recipePath = SAMPLE_RECIPES_PATH.resolve(filename);

        ComponentRecipe recipe = DESERIALIZER_JSON.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyRecipeWithAllFields(recipe);
    }

    void verifyRecipeWithAllFields(final ComponentRecipe recipe) {
        assertThat(recipe.getComponentName(), Is.is("FooService"));
        assertThat(recipe.getVersion()
                .getValue(), Is.is("1.0.0"));
        assertThat(recipe.getComponentType(), Is.is(ComponentType.PLUGIN));
        assertThat(recipe.getManifests()
                .size(), Is.is(2));
        PlatformSpecificManifest manifest = recipe.getManifests()
                .get(0);
        assertThat(manifest.getPlatform()
                .getOs(), Is.is("windows"));
        assertThat(manifest.getPlatform()
                .getArchitecture(), Is.is("x86_64"));
        assertThat(manifest.getPlatform()
                .getOsVersion(), Is.is("^10.0.1"));
        assertThat(manifest.getLifecycle()
                .size(), Is.is(1));
        assertThat(manifest.getLifecycle(), IsMapContaining.hasKey("install"));

        // verify param
        List<ComponentParameter> parameters = manifest.getParameters();
        assertThat(parameters
                .size(), Is.is(1));
        ComponentParameter testParam = parameters.get(0);
        assertThat(testParam.getName(), Is.is("TestParam"));
        assertThat(testParam.getValue(), Is.is("TestValue"));
        assertThat(testParam.getType(), Is.is(ComponentParameter.ParameterType.STRING));

        assertThat(manifest.getArtifacts()
                .size(), Is.is(2));
        ComponentArtifact artifact = manifest.getArtifacts()
                .get(0);
        assertThat(artifact.getUri()
                .toString(), Is.is("s3://some-bucket/hello_world.py"));
        assertThat(artifact.getDigest(), Is.is("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f"));
        assertThat(artifact.getAlgorithm(), Is.is("SHA-256"));
        assertThat(artifact.getUnarchive(), Is.is(Unarchive.ZIP));
        assertThat(manifest.getDependencies()
                .size(), Is.is(2));
        assertThat(manifest.getDependencies(), IsMapContaining.hasEntry("BarService",
                new DependencyProperties.DependencyPropertiesBuilder().versionRequirement("^1.1")
                        .dependencyType("soft")
                        .build()));
        assertThat(manifest.getDependencies(), IsMapContaining.hasEntry("BazService",
                new DependencyProperties.DependencyPropertiesBuilder().versionRequirement("^2.0")
                        .build()));

        manifest = recipe.getManifests()
                .get(1);
        assertThat(manifest.getPlatform(), IsNull.nullValue());
        assertThat(manifest.getLifecycle()
                .size(), Is.is(1));
        assertThat(manifest.getLifecycle(), IsMapContaining.hasKey("start"));
        assertThat(manifest.getArtifacts()
                .size(), Is.is(1));
        artifact = manifest.getArtifacts()
                .get(0);
        assertThat(artifact.getUri()
                .toString(), Is.is("s3://some-bucket/hello_world.py"));
        assertThat(artifact.getDigest(), Is.is("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f"));
        assertThat(artifact.getAlgorithm(), Is.is("SHA-256"));
        assertThat(manifest.getDependencies()
                .size(), Is.is(1));
        assertThat(manifest.getDependencies(), IsMapContaining.hasEntry("BazService",
                new DependencyProperties.DependencyPropertiesBuilder().versionRequirement("^2.0")
                        .build()));
    }

    @Test
    void GIVEN_a_wrapper_component_recipe_yaml_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "wrapper-component-recipe.yaml";
        Path recipePath = SAMPLE_RECIPES_PATH.resolve(filename);

        ComponentRecipe recipe = DESERIALIZER_YAML.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyWrapperComponentRecipe(recipe);
    }

    @Test
    void GIVEN_a_wrapper_component_recipe_json_WHEN_attempt_to_deserialize_THEN_return_instantiated_model_instance() throws IOException {
        String filename = "wrapper-component-recipe.json";
        Path recipePath = SAMPLE_RECIPES_PATH.resolve(filename);

        ComponentRecipe recipe = DESERIALIZER_JSON.readValue(new String(Files.readAllBytes(recipePath)),
                ComponentRecipe.class);

        verifyWrapperComponentRecipe(recipe);
    }

    void verifyWrapperComponentRecipe(final ComponentRecipe recipe) {
        assertThat(recipe.getComponentName(), Is.is("PythonRuntime"));
        assertThat(recipe.getVersion()
                .getValue(), Is.is("1.1.0"));
        assertThat(recipe.getManifests()
                .size(), Is.is(1));
        PlatformSpecificManifest manifest = recipe.getManifests()
                .get(0);
        assertThat(manifest.getPlatform()
                .getOs(), Is.is("linux"));
        assertThat(manifest.getPlatform()
                .getArchitecture(), Is.is("x86_64"));
        assertThat(manifest.getPlatform()
                .getOsVersion(), Is.is("^4.8"));
        assertThat(manifest.getLifecycle()
                .size(), Is.is(1));
        assertThat(manifest.getLifecycle(), IsMapContaining.hasEntry("install", "apt-get install python3.7"));
    }
}
