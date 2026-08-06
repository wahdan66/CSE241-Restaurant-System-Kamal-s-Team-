package com.restaurant.util;

import com.restaurant.exception.ValidationException;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

    public static void validateNotNullOrEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    public static void validatePositiveNumber(double value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero.");
        }
    }

    public static void validateNonNegativeNumber(double value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName + " cannot be negative.");
        }
    }

    public static void validatePhoneNumber(String phone) {
        validateNotNullOrEmpty(phone, "Phone Number");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Invalid phone number format.");
        }
    }

    public static void validatePasswordStrength(String password) {
        validateNotNullOrEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
    }
}