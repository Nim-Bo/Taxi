package com.taxiuser.exception;

public class SMSFailed extends RuntimeException {
    public SMSFailed() {
        super("SMS Failed");
    }
}
