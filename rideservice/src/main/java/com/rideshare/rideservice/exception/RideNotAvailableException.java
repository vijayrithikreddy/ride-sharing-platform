package com.rideshare.rideservice.exception;

public class RideNotAvailableException extends RuntimeException {
    public RideNotAvailableException(String message) {
        super(message);
    }
}
