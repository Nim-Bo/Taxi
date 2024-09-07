package com.taxiuser.exception;

public class Unauthorized extends RuntimeException {
    public Unauthorized() {
        super("Unauthorized");
    }
}
