package com.amazon.aws.iot.greengrass.component.common;

import com.amazon.aws.iot.greengrass.component.common.Platform.Architecture;
import com.amazon.aws.iot.greengrass.component.common.Platform.OS;

import java.util.List;
import java.util.Optional;

public final class PlatformHelper {

    private PlatformHelper() {
    }

    /**
     * find best match from a list of recipes.
     *
     * @param currentPlatform the platform detail
     * @param recipeList      a list of recipe input
     * @return closest recipe
     */
    public static Optional<PlatformSpecificManifest> findBestMatch(Platform currentPlatform,
                                                                   List<PlatformSpecificManifest> recipeList) {
        for (PlatformSpecificManifest manifest : recipeList) {
            Platform manifestPlatform = manifest.getPlatform();
            if (manifestPlatform == null) {
                // Missing platform config means this manifest works on all platforms.
                return Optional.of(manifest);
            }
            if (!OS.ALL.equals(manifestPlatform.getOs())
                    && !manifestPlatform.getOs().equals(currentPlatform.getOs())) {
                continue;
            }
            if (!Architecture.ALL.equals(manifestPlatform.getArchitecture())
                    && !manifestPlatform.getArchitecture().equals(currentPlatform.getArchitecture())) {
                continue;
            }

            return Optional.of(manifest);
        }

        return Optional.empty();
    }

}
