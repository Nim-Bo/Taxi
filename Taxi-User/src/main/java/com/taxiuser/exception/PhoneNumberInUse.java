package com.taxiuser.exception;

public class PhoneNumberInUse extends RuntimeException {
    public PhoneNumberInUse() {
        super("Phone number is already in use");
    }
}
