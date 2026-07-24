package com.rideshare.rideservice.exception;

public class RideRequestAlreadyExistsException extends RuntimeException {
    public RideRequestAlreadyExistsException(String message) {
        super(message);
    }
}
