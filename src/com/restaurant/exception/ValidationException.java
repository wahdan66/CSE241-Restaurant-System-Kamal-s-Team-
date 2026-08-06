package com.restaurant.exception;

/** Signals that supplied input fails a required format or value check. */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
