package com.enterprise.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ValidationUtil}.
 *
 * @author Enterprise Team
 * @version 1.0.0
 * @since 2026-01-01
 */
class ValidationUtilTest {

    @Test
    void testIsValidEmailWithValidEmail() {
        // Given
        String validEmail = "user@example.com";

        // When
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsValidEmailWithValidEmailContainingPlus() {
        // Given
        String validEmail = "user+tag@example.com";

        // When
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsValidEmailWithValidEmailContainingDot() {
        // Given
        String validEmail = "first.last@example.com";

        // When
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsValidEmailWithInvalidEmailNoAtSign() {
        // Given
        String invalidEmail = "userexample.com";

        // When
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsValidEmailWithInvalidEmailNoDomain() {
        // Given
        String invalidEmail = "user@";

        // When
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsValidEmailWithNull() {
        // When
        boolean result = ValidationUtil.isValidEmail(null);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsValidEmailWithBlankString() {
        // When
        boolean result = ValidationUtil.isValidEmail("   ");

        // Then
        assertFalse(result);
    }

    @Test
    void testIsValidEmailWithEmptyString() {
        // When
        boolean result = ValidationUtil.isValidEmail("");

        // Then
        assertFalse(result);
    }

    @Test
    void testIsNullOrEmptyWithNull() {
        // When
        boolean result = ValidationUtil.isNullOrEmpty(null);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsNullOrEmptyWithEmptyString() {
        // When
        boolean result = ValidationUtil.isNullOrEmpty("");

        // Then
        assertTrue(result);
    }

    @Test
    void testIsNullOrEmptyWithWhitespace() {
        // When
        boolean result = ValidationUtil.isNullOrEmpty("   ");

        // Then
        assertTrue(result);
    }

    @Test
    void testIsNullOrEmptyWithValidString() {
        // When
        boolean result = ValidationUtil.isNullOrEmpty("valid");

        // Then
        assertFalse(result);
    }

    @Test
    void testRequireNonNullWithValidObject() {
        // Given
        String validObject = "test";

        // When/Then - Should not throw
        assertDoesNotThrow(() -> ValidationUtil.requireNonNull(validObject, "testField"));
    }

    @Test
    void testRequireNonNullWithNullObject() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtil.requireNonNull(null, "userId")
        );

        // Then
        assertEquals("userId cannot be null", exception.getMessage());
    }

    @Test
    void testRequireNonNullWithDifferentFieldName() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtil.requireNonNull(null, "emailAddress")
        );

        // Then
        assertEquals("emailAddress cannot be null", exception.getMessage());
    }

    @Test
    void testUtilityClassCannotBeInstantiated() {
        // When/Then
        Exception exception = assertThrows(Exception.class, () -> {
            var constructor = ValidationUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // The UnsupportedOperationException is wrapped in InvocationTargetException
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    }
}
