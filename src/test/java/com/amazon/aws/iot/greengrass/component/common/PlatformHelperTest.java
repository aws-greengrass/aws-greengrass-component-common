package com.amazon.aws.iot.greengrass.component.common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PlatformHelperTest {

    @Test
    public void GIVEN_platform_WHEN_findBestMatch_THEN_correct_recipe_returned() throws Exception {
        Map<String,String> platformToTest = PlatformBuilder.of()
                .os("linux")
                .architecture("amd64")
                .platform();

        PlatformSpecificManifest recipeCandidate1 = PlatformBuilder.of()
                .os("linux")
                .architecture("amd64")
                .manifest();

        PlatformSpecificManifest recipeCandidate2 = PlatformBuilder.of()
                .architecture("amd64")
                .os("//")
                .manifest();

        PlatformSpecificManifest recipeCandidate3 = PlatformBuilder.of()
                .architecture("amd64")
                .manifest();

        PlatformSpecificManifest recipeCandidate4 = PlatformSpecificManifest.builder()
                .platform(null).build();

        PlatformSpecificManifest recipeCandidate_notApplicable1 = PlatformBuilder.of()
                .os("windows")
                .manifest();
        PlatformSpecificManifest recipeCandidate_notApplicable2 = PlatformBuilder.of()
                .architecture("something-else")
                .manifest();

        Optional<PlatformSpecificManifest> result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidate1,
                recipeCandidate2,
                recipeCandidate3,
                recipeCandidate4,
                recipeCandidate_notApplicable1));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidate1, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidate2,
                recipeCandidate3,
                recipeCandidate4,
                recipeCandidate_notApplicable1));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidate2, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidate3,
                recipeCandidate4,
                recipeCandidate_notApplicable1));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidate3, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidate4,
                recipeCandidate_notApplicable1));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidate4, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidate_notApplicable1,
                recipeCandidate_notApplicable2));

        assertFalse(result.isPresent());
    }
}
