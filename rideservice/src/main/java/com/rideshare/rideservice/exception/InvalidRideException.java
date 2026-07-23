package com.rideshare.rideservice.exception;

public class InvalidRideException extends RuntimeException {
    public InvalidRideException(String message) {
        super(message);
    }
}
