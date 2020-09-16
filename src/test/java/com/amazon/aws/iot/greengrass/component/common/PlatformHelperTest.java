package com.amazon.aws.iot.greengrass.component.common;

import com.amazon.aws.iot.greengrass.component.common.Platform.Architecture;
import com.amazon.aws.iot.greengrass.component.common.Platform.OS;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PlatformHelperTest {

    @Test
    public void GIVEN_platform_WHEN_findBestMatch_THEN_correct_recipe_returned() throws Exception {
        Platform platformToTest = Platform.builder()
                .os(OS.LINUX)
                .architecture(Architecture.AMD64)
                .build();

        PlatformSpecificManifest recipeCandidate1 = createRecipeForPlatform(Platform.builder()
                .os(OS.LINUX)
                .architecture(Architecture.AMD64)
                .build());

        PlatformSpecificManifest recipeCandidate2 = createRecipeForPlatform(Platform.builder()
                .architecture(Architecture.AMD64)
                .os(OS.ALL)
                .build());

        PlatformSpecificManifest recipeCandidate3 = createRecipeForPlatform(Platform.builder()
                .architecture(Architecture.AMD64)
                .build());

        PlatformSpecificManifest recipeCandidate4 = createRecipeForPlatform(null);

        PlatformSpecificManifest recipeCandidate_notApplicable1 = createRecipeForPlatform(Platform.builder()
                .os(OS.WINDOWS)
                .build());
        PlatformSpecificManifest recipeCandidate_notApplicable2 = createRecipeForPlatform(Platform.builder()
                .architecture(Architecture.ARM)
                .build());

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

    private PlatformSpecificManifest createRecipeForPlatform(Platform platform) {
        return PlatformSpecificManifest.builder()
                .platform(platform).build();
    }
}
