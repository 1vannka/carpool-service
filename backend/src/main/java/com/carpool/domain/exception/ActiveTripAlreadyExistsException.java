package com.carpool.domain.exception;

public class ActiveTripAlreadyExistsException extends RuntimeException {
    public ActiveTripAlreadyExistsException(String message) {
        super(message);
    }
}
