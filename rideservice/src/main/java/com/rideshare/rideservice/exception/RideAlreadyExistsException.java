package com.rideshare.rideservice.exception;

public class RideAlreadyExistsException extends RuntimeException {
    public RideAlreadyExistsException(String message) {
        super(message);
    }
}
