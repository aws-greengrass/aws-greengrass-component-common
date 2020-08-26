package com.amazon.aws.iot.greengrass.component.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentArtifact {
    URI uri;

    String digest;

    String algorithm;

    Unarchive unarchive;
}
