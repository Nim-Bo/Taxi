package com.taxiuser.exception;

import org.apache.coyote.BadRequestException;

public class InvalidVerificationCode extends BadRequestException {
    public InvalidVerificationCode() {
        super("Invalid verification code");
    }
}
