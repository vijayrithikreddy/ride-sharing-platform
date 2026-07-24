package com.rideshare.rideservice.exception;

public class InvalidRideRequestException extends RuntimeException {
    public InvalidRideRequestException(String message) {
        super(message);
    }
}
