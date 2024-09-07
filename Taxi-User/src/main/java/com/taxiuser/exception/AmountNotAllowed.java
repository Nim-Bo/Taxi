package com.taxiuser.exception;

import org.apache.coyote.BadRequestException;

public class AmountNotAllowed extends BadRequestException {
    public AmountNotAllowed() {
        super("Amount must be greater than 0");
    }
}
