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
     * @param targetPlatform the platform detail (set of key/value pairs), where each value is a label
     * @param manifestList      a list of recipe manifests
     * @return closest manifest
     */
    public static Optional<PlatformSpecificManifest> findBestMatch(Map<String, String> targetPlatform,
                                                                   List<PlatformSpecificManifest> manifestList) {
        return manifestList.stream().filter(m -> testPlatform(targetPlatform, m)).findFirst();
    }

    private static boolean testPlatform(Map<String, String> targetPlatform, PlatformSpecificManifest manifest) {
        Map<String, String> platformMatch = manifest.getPlatform(); // contains match expressions
        if (platformMatch == null || platformMatch.isEmpty()) {
            return true; // no platform is considered a wild-card
        }
        return platformMatch.entrySet().stream().allMatch(e ->
                testField(e.getKey(), targetPlatform.get(e.getKey()), e.getValue()));
    }

    /**
     * Test a single platform match expression
     * @param name     Field name (some names may change match behavior)
     * @param label    Platform label (as provided by target platform, null if not defined)
     * @param template Template (a string)
     * @return true if single field matches
     */
    private static boolean testField(String name, String label, String template) {
        if (template == null) {
            return label == null; // allow explicit null (no-value) test
        }
        if (label == null) {
            return false; // in all other cases, label must not be null
        }
        if (template.equals(Platform.WILDCARD)) {
            return true; // explicit non-null (has value) test (actual non-null was tested above)
        }
        if (template.length() >= 2 && template.startsWith("/") && template.endsWith("/")) {
            return label.matches(template.substring(1, template.length()-1));
        }
        // TODO: Remove for re:Invent. This is here to maintain existing behavior
        // Going forward, use "*" (wildcard)
        if ("os".equals(name) && ("all".equals(template) || "any".equals(template))) {
            return true; // treat as wildcard
        }
        // TODO: Remove for re:Invent. This is here to maintain existing behavior
        // Going forward, use "*" (wildcard)
        if ("architecture".equals(name) && ("all".equals(template) || "any".equals(template))) {
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
