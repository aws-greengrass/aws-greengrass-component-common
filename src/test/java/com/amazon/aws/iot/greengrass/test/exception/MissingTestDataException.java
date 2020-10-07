package com.amazon.aws.iot.greengrass.test.exception;

public class MissingTestDataException extends RuntimeException {
    public MissingTestDataException(String message, Exception e) {
        super(message, e);
    }
}
