package com.taxiuser.exception;

import org.apache.coyote.BadRequestException;

public class UserIsDriving extends BadRequestException {
    public UserIsDriving() {
        super("You are in driving mode");
    }
}
