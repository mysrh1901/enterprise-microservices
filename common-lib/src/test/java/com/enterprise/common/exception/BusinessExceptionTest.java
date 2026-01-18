package com.enterprise.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BusinessException}.
 *
 * @author Enterprise Team
 * @version 1.0.0
 * @since 2026-01-01
 */
class BusinessExceptionTest {

    @Test
    void testExceptionWithMessage() {
        // Given
        String message = "Business rule violation";

        // When
        BusinessException exception = new BusinessException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals("BUSINESS_ERROR", exception.getErrorCode());
    }

    @Test
    void testExceptionWithMessageAndErrorCode() {
        // Given
        String message = "Invalid amount";
        String errorCode = "INVALID_AMOUNT";

        // When
        BusinessException exception = new BusinessException(message, errorCode);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    void testExceptionWithCause() {
        // Given
        String message = "Processing failed";
        String errorCode = "PROCESSING_ERROR";
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // When
        BusinessException exception = new BusinessException(message, errorCode, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionIsRuntimeException() {
        // Given
        BusinessException exception = new BusinessException("Error");

        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}
