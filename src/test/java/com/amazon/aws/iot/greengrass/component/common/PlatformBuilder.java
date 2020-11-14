package com.amazon.aws.iot.greengrass.component.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * For use by tests only
 */
public class PlatformBuilder {
    private Platform platform = new Platform();

    PlatformBuilder add(String field, String value) {
        platform.put(field, value);
        return this;
    }

    PlatformBuilder os(Platform.OS value) {
        return add("os", value.getName());
    }

    PlatformBuilder architecture(Platform.Architecture value) {
        return add("architecture", value.getName());
    }

    Map<String, String> reference() {
        return platform;
    }

    PlatformSpecificManifest manifest() {
        return PlatformSpecificManifest.builder()
                .platform(platform).build();
    }

    static PlatformBuilder of() {
        return new PlatformBuilder();
    }
}
