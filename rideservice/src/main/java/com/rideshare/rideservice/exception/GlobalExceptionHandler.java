package com.rideshare.rideservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RideAlreadyExistsException.class)
    public ResponseEntity<String> handleRideAlreadyExistsException(
            RideAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRideException.class)
    public ResponseEntity<String> handleInvalidRideException(
            InvalidRideException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
    @ExceptionHandler(InvalidRideStateException.class)
    public ResponseEntity<String> handleInvalidRideStateException(InvalidRideStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
    @ExceptionHandler(UnauthorizedRideAccessException.class)
    public ResponseEntity<String> handleUnauthorizedRideAccessException(
            UnauthorizedRideAccessException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RideRequestNotFoundException.class)
    public ResponseEntity<String> handleRideRequestNotFoundException(
            RideRequestNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RideRequestAlreadyExistsException.class)
    public ResponseEntity<String> handleRideRequestAlreadyExistsException(
            RideRequestAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RideNotAvailableException.class)
    public ResponseEntity<String> handleRideNotAvailableException(
            RideNotAvailableException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRideRequestStateException.class)
    public ResponseEntity<String> handleInvalidRideRequestStateException(
            InvalidRideRequestStateException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRideRequestException.class)
    public ResponseEntity<String> handleInvalidRideRequestException(
            InvalidRideRequestException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<String> handleRideNotFoundException(RideNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}