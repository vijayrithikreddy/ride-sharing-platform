package com.rideshare.rideservice.exception;

public class UnauthorizedRideAccessException extends RuntimeException {
    public UnauthorizedRideAccessException(String message) {
        super(message);
    }
}
