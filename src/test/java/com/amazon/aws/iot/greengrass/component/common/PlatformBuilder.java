package com.amazon.aws.iot.greengrass.component.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * For use by tests only
 */
public class PlatformBuilder {
    Map<String, Object> fields = new HashMap<>();

    PlatformBuilder add(String field, Object value) {
        fields.put(field, value);
        return this;
    }

    PlatformBuilder os(Object value) {
        return add("os", value);
    }

    PlatformBuilder architecture(Object value) {
        return add("architecture", value);
    }

    Map<String, String> platform() {
        return Collections.unmodifiableMap(fields.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(),
                e-> (String)e.getValue())));
    }

    Map<String, Object> platformMatch() {
        return Collections.unmodifiableMap(fields.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(),
                e-> e.getValue())));
    }

    PlatformSpecificManifest manifest() {
        return PlatformSpecificManifest.builder()
                .platform(platformMatch()).build();
    }

    static PlatformBuilder of() {
        return new PlatformBuilder();
    }
}
