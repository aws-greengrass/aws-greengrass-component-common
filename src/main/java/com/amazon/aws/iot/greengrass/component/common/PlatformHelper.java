package com.amazon.aws.iot.greengrass.component.common;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public final class PlatformHelper {

    // this test exists so to allow future extension, a platform label should not start with special characters
    private static final Pattern SIMPLE_LABEL = Pattern.compile("^[a-zA-Z0-9]");

    private PlatformHelper() {
    }

    /**
     * find best match from a list of recipes.
     *
     * @param targetPlatform Platform attributes to test against (usually the actual platform of the device).
     * @param manifestList   A list of recipe manifests.
     * @return first matching manifest.
     */
    public static Optional<PlatformSpecificManifest> findBestMatch(Map<String, String> targetPlatform,
                                                                   List<PlatformSpecificManifest> manifestList) {
        //
        // Manifests are listed in order of preference, so the first match is the relevant match
        //
        return manifestList.stream().filter(m -> isRequirementSatisfied(targetPlatform, m)).findFirst();
    }

    /**
     * Test that the requirements section of a manifest is satisfied.
     * @param targetPlatform Platform to test against (usually the actual platform of the device).
     * @param manifest Single manifest
     * @return
     */
    private static boolean isRequirementSatisfied(Map<String, String> targetPlatform, PlatformSpecificManifest manifest) {
        //
        // The "requirement" section of the Manifest contains a map of attribute:template
        // Note that it is important that an attribute is permitted to be in targetPlatform but not in requirement,
        // which will happen as we add more attributes to match against. Therefore all requirements must be
        // satisfied, but not all target attributes need to have a requirement template.
        //
        Map<String, String> platformRequirement = manifest.getPlatform();
        if (platformRequirement == null || platformRequirement.isEmpty()) {
            return true; // no platform is considered a wild-card
        }
        return platformRequirement.entrySet().stream().allMatch(e ->
                isAttributeSatisfied(e.getKey(), targetPlatform.get(e.getKey()), e.getValue()));
    }

    /**
     * Test a single field expression is satisfied.
     *
     * @param name     Attribute name (some names may change match behavior)
     * @param label    Platform label (as provided by target platform, null if not defined)
     * @param template Template (a string)
     * @return true if attribute requirement is satisfied
     */
    private static boolean isAttributeSatisfied(String name, String label, String template) {
        if (template == null || template.equals(Platform.WILDCARD)) {
            // treat null same as missing template entry.
            // treat both as same as wildcard
            return true;
        }
        if (label == null || label.length() == 0) {
            // in all other cases, label must not be a null / missing / blank
            // (each indicate no value)
            return false;
        }
        if (template.length() >= 2 && template.startsWith("/") && template.endsWith("/")) {
            // regular expression match, such as for alternatives
            return label.matches(template.substring(1, template.length()-1));
        }
        // TODO: Remove for re:Invent. This is here to maintain existing behavior
        // Going forward, use "*" (wildcard)
        if (Platform.OS_KEY.equals(name) && ("all".equals(template) || "any".equals(template))) {
            return true; // treat as wildcard
        }
        // TODO: Remove for re:Invent. This is here to maintain existing behavior
        // Going forward, use "*" (wildcard)
        if (Platform.ARCHITECTURE_KEY.equals(name) && ("all".equals(template) || "any".equals(template))) {
            return true; // treat as wildcard
        }
        // Other special symbols may be implemented here, so we permit only simple labels for platform matching
        if (!SIMPLE_LABEL.matcher(template).lookingAt()) {
            // reject any special labels, to allow future extension
            // Review note, how to log?
            return false;
        }
        return template.equals(label);
    }
}
