package com.rideshare.rideservice.exception;

public class InvalidRideRequestStateException extends RuntimeException {
    public InvalidRideRequestStateException(String message) {
        super(message);
    }
}
