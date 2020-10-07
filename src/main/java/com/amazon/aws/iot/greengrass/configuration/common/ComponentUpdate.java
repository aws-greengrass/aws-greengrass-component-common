package com.amazon.aws.iot.greengrass.configuration.common;

import com.amazon.aws.iot.greengrass.component.common.SemverSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdurmont.semver4j.Semver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonSerialize
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class ComponentUpdate {

    @JsonSerialize(using = SemverSerializer.class)
    private Semver version;

    private ConfigurationUpdate configurationUpdate;
}
