/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0 */

package com.amazon.aws.iot.greengrass.component.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.vdurmont.semver4j.Requirement;

import java.io.IOException;

public class RequirementSerializer extends StdSerializer<Requirement> {
    public RequirementSerializer() {
        super(Requirement.class);
    }

    @Override
    public void serialize(final Requirement value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final String version = value.toString();
        gen.writeString(version);
    }
}
