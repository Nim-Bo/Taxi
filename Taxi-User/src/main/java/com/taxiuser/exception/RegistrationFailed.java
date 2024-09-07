package com.taxiuser.exception;

import org.apache.coyote.BadRequestException;

public class RegistrationFailed extends BadRequestException {
    public RegistrationFailed(String message) {
        super(message);
    }
}
