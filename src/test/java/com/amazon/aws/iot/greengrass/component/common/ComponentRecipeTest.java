package com.amazon.aws.iot.greengrass.component.common;


import com.vdurmont.semver4j.Semver;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ComponentRecipeTest {

    @Test
    void GIVEN_component_name_empty_WHEN_build_component_recipe_THEN_throw_illegal_argument_exception() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ComponentRecipe.builder()
                .componentName("")
                .componentVersion(new Semver("1.0.0"))
                .build());

        assertThat(exception.getMessage(), Is.is("Component name is empty"));
    }

    @Test
    void GIVEN_long_component_name_WHEN_build_component_recipe_THEN_throw_illegal_argument_exception() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ComponentRecipe.builder()
                .componentName(
                        "Long_component_name_exceed_limit_Long_component_name_exceed_limit_Long_component_name_exceed_limit")
                .componentVersion(new Semver("1.0.0"))
                .build());

        assertThat(exception.getMessage(), containsString("Component name length exceeds"));
    }

    @Test
    void GIVEN_component_name_contain_illegal_character_WHEN_build_component_recipe_THEN_throw_illegal_argument_exception() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ComponentRecipe.builder()
                .componentName("component_name_with space")  // space is illegal in component name
                .componentVersion(new Semver("1.0.0"))
                .build());

        assertThat(exception.getMessage(), containsString("Component name could only include characters"));
    }

    @Test
    void GIVEN_component_name_contain_legal_characters_WHEN_build_component_recipe_THEN_return_correct_model() {
        ComponentRecipe recipe = ComponentRecipe.builder()
                .recipeFormatVersion(RecipeFormatVersion.JAN_25_2020)
                .componentName("aws.greengrass.foo-bar_baz")
                .componentVersion(new Semver("1.0.0"))
                .build();

        assertThat(recipe.getComponentName(), Is.is("aws.greengrass.foo-bar_baz"));
    }

    @Test
    void GIVEN_long_component_version_WHEN_build_component_recipe_THEN_throw_illegal_argument_exception() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ComponentRecipe.builder()
                .componentName("foo")
                .componentVersion(new Semver("1.0.0-alpha.beta.gamma.alpha.beta" +
                        ".gamma+21AF26D3—-117B344092BD21AF26D3—-117B344092BD"))
                .build());

        assertThat(exception.getMessage(), containsString("Component version length exceeds"));
    }

    @Test
    void GIVEN_component_major_version_over_limit_WHEN_build_component_recipe_THEN_throw_illegal_argument_exception() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ComponentRecipe.builder()
                .componentName("foo")
                .componentVersion(new Semver("1000000.0.0"))
                .build());

        assertThat(exception.getMessage(), Is.is("Component version major, minor, patch can't exceed 6 digits"));
    }

    @Test
    void GIVEN_component_minor_version_over_limit_WHEN_build_component_recipe_THEN_throw_illegal_argument_exception() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ComponentRecipe.builder()
                .componentName("foo")
                .componentVersion(new Semver("1.1000000.0."))
                .build());

        assertThat(exception.getMessage(), Is.is("Component version major, minor, patch can't exceed 6 digits"));
    }

    @Test
    void GIVEN_component_patch_version_over_limit_WHEN_build_component_recipe_THEN_throw_illegal_argument_exception() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ComponentRecipe.builder()
                .componentName("foo")
                 .componentVersion(new Semver("1.1.1000000"))
                .build());

        assertThat(exception.getMessage(), Is.is("Component version major, minor, patch can't exceed 6 digits"));
    }

    @Test
    void GIVEN_legal_component_version_WHEN_build_component_recipe_THEN_return_correct_model() {
        ComponentRecipe recipe = ComponentRecipe.builder()
                .recipeFormatVersion(RecipeFormatVersion.JAN_25_2020)
                .componentName("foo")
                .componentVersion(new Semver("1.0.0-alpha.beta.gamma+21AF26D3—-117B344092BD21A"))
                .build();

        assertThat(recipe.getComponentVersion().getValue(), Is.is("1.0.0-alpha.beta.gamma+21AF26D3—-117B344092BD21A"));
    }
}
