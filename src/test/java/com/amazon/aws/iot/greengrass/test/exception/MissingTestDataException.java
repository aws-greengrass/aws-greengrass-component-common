/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.aws.iot.greengrass.test.exception;

public class MissingTestDataException extends RuntimeException {
    public MissingTestDataException(String message, Exception e) {
        super(message, e);
    }
}
