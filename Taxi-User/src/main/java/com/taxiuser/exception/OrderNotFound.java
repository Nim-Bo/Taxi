package com.taxiuser.exception;

import org.apache.coyote.BadRequestException;

public class OrderNotFound extends BadRequestException {
    public OrderNotFound() {
        super("Order not found");
    }
}
