package com.amazon.aws.iot.greengrass.component.common;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public final class PlatformHelper {

    // this test exists so to allow future extension, a platform label should not start with special characters
    private static Pattern SIMPLE_LABEL = Pattern.compile("^[a-zA-Z0-9]");

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
        Map<String, Object> platformMatch = manifest.getPlatform();
        if (platformMatch == null || platformMatch.isEmpty()) {
            return true; // no platform is considered a wild-card
        }
        return platformMatch.entrySet().stream().allMatch(e ->
                testAlternatives(e.getKey(), targetPlatform.get(e.getKey()), e.getValue()));
    }

    /**
     * Test a label against a rich specification of alternatives for that field
     * @param name     Field name (some names may change match behavior)
     * @param label    Platform label (as provided by target platform, null if not defined)
     * @param template Template (typically a string or list)
     * @return true if single field matches
     */
    private static boolean testAlternatives(String name, String label, Object template) {
        // Note, label can be null. It's nullness is tested in testSingle, this has consequence that
        // template == [] will match against null value, whereas template == "//" will not.
        if (template instanceof List) {
            // If list, this is a list of alternatives
            // Note, [single] is allowed, which may be useful in future to allow [[something]] such as to allow
            // version range matches
            return ((List<?>)template).stream().anyMatch(v -> testField(name, label, v));
        } else {
            // If not a list, no alternatives.
            return testField(name, label, template);
        }
    }

    /**
     * Test a single platform match expression
     * @param name     Field name (some names may change match behavior)
     * @param label    Platform label (as provided by target platform, null if not defined)
     * @param template Template (typically a string)
     * @return true if single field matches
     */
    private static boolean testField(String name, String label, Object template) {
        if (template == null) {
            return label == null; // allow explicit null (no-value) test
        }
        if (label == null) {
            return false; // in all other cases, label must not be null
        }
        if (template instanceof List) {
            // reserved for future (potentially [[version-range]] )
            return false;
        }
        if (! (template instanceof String)) {
            // integers, floats, maps, etc, not supported (yet)
            return false;
        }
        String templateString = (String)template;
        if (templateString.equals("//")) {
            return true; // explicit non-null (has value) test (actual non-null was tested above)
        }
        if (templateString.length() > 2 && templateString.startsWith("/") && templateString.endsWith("/")) {
            // Note, "//" is wild-card explicitly handled above, so length > 2 is intentional
            return label.matches(templateString.substring(1, templateString.length()-1));
        }
        // TODO: Remove for re:Invent. This is here to maintain existing behavior
        // Going forward, use "//" (wildcard)
        if ("os".equals(name) && ("all".equals(templateString) || "any".equals(templateString))) {
            return true; // treat as wildcard
        }
        // TODO: Remove for re:Invent. This is here to maintain existing behavior
        // Going forward, use "//" (wildcard)
        if ("architecture".equals(name) && ("all".equals(templateString) || "any".equals(templateString))) {
            return true; // treat as wildcard
        }
        // Other special symbols may be implemented here, so we permit only simple labels for platform matching
        if (!SIMPLE_LABEL.matcher(templateString).matches()) {
            // reject any special labels, to allow future extension
            // Review note, how to log?
            return false;
        }
        return templateString.equals(label);
    }
}
