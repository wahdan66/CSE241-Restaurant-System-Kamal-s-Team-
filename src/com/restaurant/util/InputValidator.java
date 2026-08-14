package com.restaurant.util;

import com.restaurant.exception.ValidationException;

import java.util.regex.Pattern;

/** Contains reusable validation checks for values entered by users. */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    // Update PHONE_PATTERN in InputValidator.java:
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,20}$");

    public static void validateNotNullOrEmpty(String value, String fieldName) {
        // trim() treats whitespace-only text as missing input.
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    public static void validatePositiveNumber(double value, String fieldName) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new ValidationException(fieldName + " must be a finite number greater than zero.");
        }
    }

    public static void validateNonNegativeNumber(double value, String fieldName) {
        if (!Double.isFinite(value) || value < 0) {
            throw new ValidationException(fieldName + " must be a finite non-negative number.");
        }
    }

    public static void validatePhoneNumber(String phone) {
        // Validate presence before applying the phone-number pattern.
        validateNotNullOrEmpty(phone, "Phone Number");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Invalid phone number format.");
        }
    }

    public static void validatePasswordStrength(String password) {
        // Validate presence before checking the minimum permitted length.
        validateNotNullOrEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
    }
}
