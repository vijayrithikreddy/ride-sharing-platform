package com.rideshare.userservice.exception;

public class ProfileAlreadyCreatedException extends RuntimeException {
    public ProfileAlreadyCreatedException(String message) {
        super(message);
    }
}
