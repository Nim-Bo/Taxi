package com.taxiuser.exception;

import org.apache.coyote.BadRequestException;

public class IncompleteOrderExists extends BadRequestException {
    public IncompleteOrderExists() {
        super("Incomplete Order Exists");
    }
}
