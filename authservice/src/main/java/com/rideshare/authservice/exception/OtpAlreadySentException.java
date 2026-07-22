package com.rideshare.authservice.exception;

public class OtpAlreadySentException extends RuntimeException {
    public OtpAlreadySentException(String message) {
        super(message);
    }
}
