package com.rideshare.rideservice.exception;

public class RideRequestNotFoundException extends RuntimeException {
    public RideRequestNotFoundException(String message) {
        super(message);
    }
}
