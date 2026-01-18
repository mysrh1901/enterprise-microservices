package com.enterprise.common.util;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void requireNonNull(Object object, String fieldName) {
        if (object == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
}
