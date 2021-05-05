package com.amazon.aws.iot.greengrass.deployment.common;

import com.amazon.aws.iot.greengrass.component.common.ComponentConfiguration;
import com.amazon.aws.iot.greengrass.configuration.common.ConfigurationUpdate;
import com.vdurmont.semver4j.Semver;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeploymentComponentConfiguration {
    String componentName;
    Semver componentVersion;
    ConfigurationUpdate configurationUpdate;
}
