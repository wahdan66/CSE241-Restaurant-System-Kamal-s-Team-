package com.restaurant.exception;

/** Signals that a requested action violates a restaurant workflow rule. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
