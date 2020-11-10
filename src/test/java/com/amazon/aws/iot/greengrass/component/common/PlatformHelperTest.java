package com.amazon.aws.iot.greengrass.component.common;

import org.junit.jupiter.api.Test;

import com.amazon.aws.iot.greengrass.component.common.Platform.Architecture;
import com.amazon.aws.iot.greengrass.component.common.Platform.OS;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PlatformHelperTest {

    @Test
    public void GIVEN_platform_WHEN_findBestMatch_THEN_correct_recipe_returned() throws Exception {
        Map<String, String> platformToTest = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .reference();

        PlatformSpecificManifest recipeCandidate1 = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .manifest();

        PlatformSpecificManifest recipeCandidate2 = PlatformBuilder.of()
                .add("os", "*")
                .add("architecture", "something")
                .manifest();

        PlatformSpecificManifest recipeCandidate3 = PlatformBuilder.of()
                .add("architecture", "something")
                .manifest();

        PlatformSpecificManifest recipeCandidate4 = PlatformSpecificManifest.builder()
                .platform(null).build();

        PlatformSpecificManifest recipeCandidate_notApplicable1 = PlatformBuilder.of()
                .add("os", "unknown")
                .manifest();
        PlatformSpecificManifest recipeCandidate_notApplicable2 = PlatformBuilder.of()
                .add("architecture", "something-else")
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

    @Test
    public void GIVEN_platform_WHEN_findBestMatch_as_regex_THEN_correct_recipe_returned() throws Exception {
        Map<String, String> platformToTest = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .reference();

        PlatformSpecificManifest recipeCandidateExact = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .manifest();

        PlatformSpecificManifest recipeCandidateSet = PlatformBuilder.of()
                .add("os", "/^one|two|other|three$/")
                .add("architecture", "something")
                .manifest();

        PlatformSpecificManifest recipeNonCandidateSet = PlatformBuilder.of()
                .add("os", "/^one|two|three$/")
                .add("architecture", "something")
                .manifest();

        Optional<PlatformSpecificManifest> result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeNonCandidateSet,
                recipeCandidateSet,
                recipeCandidateExact));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidateSet, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeNonCandidateSet,
                recipeCandidateExact,
                recipeCandidateSet));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidateExact, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeNonCandidateSet));

        assertFalse(result.isPresent());
    }

    @Test
    public void GIVEN_platform_WHEN_findBestMatch_with_new_field_THEN_correct_recipe_returned() throws Exception {
        Map<String, String> platformToTest = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .add("another", "foo")
                .reference();

        PlatformSpecificManifest recipeCandidateExact = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .add("another", "foo")
                .manifest();

        PlatformSpecificManifest recipeCandidateMismatch = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .add("another", "bar")
                .manifest();

        PlatformSpecificManifest recipeCandidateSubset = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .manifest();

        PlatformSpecificManifest recipeCandidateAny = PlatformBuilder.of()
                .add("os", "*")
                .add("architecture", "something")
                .add("another", "*")
                .manifest();

        Optional<PlatformSpecificManifest> result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidateMismatch,
                recipeCandidateExact,
                recipeCandidateSubset));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidateExact, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidateMismatch,
                recipeCandidateSubset,
                recipeCandidateExact,
                recipeCandidateAny));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidateSubset, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidateMismatch,
                recipeCandidateAny,
                recipeCandidateSubset,
                recipeCandidateExact));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidateAny, result.get());
    }

    @Test
    public void GIVEN_platform_WHEN_findBestMatch_with_missing_field_THEN_correct_recipe_returned() throws Exception {
        Map<String, String> platformToTest = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .reference();

        PlatformSpecificManifest recipeCandidateMismatch = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .add("another", "foo")
                .manifest();

        PlatformSpecificManifest recipeCandidateExact = PlatformBuilder.of()
                .add("os", "other")
                .add("architecture", "something")
                .manifest();

        PlatformSpecificManifest recipeCandidateAny = PlatformBuilder.of()
                .add("os", "*")
                .add("architecture", "something")
                .add("another", "*")
                .manifest();

        Optional<PlatformSpecificManifest> result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidateMismatch,
                recipeCandidateExact,
                recipeCandidateAny));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidateExact, result.get());

        result = PlatformHelper.findBestMatch(platformToTest, Arrays.asList(
                recipeCandidateMismatch,
                recipeCandidateAny,
                recipeCandidateExact));

        assertTrue(result.isPresent());
        assertEquals(recipeCandidateAny, result.get());
    }

    @Test
    public void GIVEN_old_platform_WHEN_findBestMatch_THEN_correct_recipe_returned() throws Exception {
        Map<String, String>  platformToTest = PlatformBuilder.of()
                .os(OS.LINUX)
                .architecture(Architecture.AMD64)
                .reference();

        PlatformSpecificManifest recipeCandidate1 = PlatformBuilder.of()
                .os(OS.LINUX)
                .architecture(Architecture.AMD64)
                .manifest();

        PlatformSpecificManifest recipeCandidate2 = PlatformBuilder.of()
                .architecture(Architecture.AMD64)
                .os(OS.ALL)
                .manifest();

        PlatformSpecificManifest recipeCandidate3 = PlatformBuilder.of()
                .architecture(Architecture.AMD64)
                .manifest();

        PlatformSpecificManifest recipeCandidate4 = PlatformSpecificManifest.builder()
                .platform(null).build();

        PlatformSpecificManifest recipeCandidate_notApplicable1 = PlatformBuilder.of()
                .os(OS.WINDOWS)
                .manifest();
        PlatformSpecificManifest recipeCandidate_notApplicable2 = PlatformBuilder.of()
                .architecture(Architecture.ARM)
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
