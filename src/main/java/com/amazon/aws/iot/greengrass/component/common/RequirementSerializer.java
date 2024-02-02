/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.component.common;

import com.amazon.aws.iot.greengrass.semver.Range;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class RequirementSerializer extends StdSerializer<Range> {
    public RequirementSerializer() {
        super(Range.class);
    }

    @Override
    public void serialize(final Range value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final String version = value.toString();
        gen.writeString(version);
    }
}
