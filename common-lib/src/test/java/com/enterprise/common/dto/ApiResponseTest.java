package com.enterprise.common.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ApiResponse}.
 *
 * @author Enterprise Team
 * @version 1.0.0
 * @since 2026-01-01
 */
class ApiResponseTest {

    @Test
    void testSuccessWithDataOnly() {
        // Given
        String testData = "Test Data";

        // When
        ApiResponse<String> response = ApiResponse.success(testData);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(testData, response.getData());
        assertNull(response.getMessage());
        assertNull(response.getErrorCode());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testSuccessWithMessageAndData() {
        // Given
        String message = "Operation successful";
        String testData = "Test Data";

        // When
        ApiResponse<String> response = ApiResponse.success(message, testData);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(testData, response.getData());
        assertNull(response.getErrorCode());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testError() {
        // Given
        String errorMessage = "An error occurred";
        String errorCode = "ERROR_CODE";

        // When
        ApiResponse<String> response = ApiResponse.error(errorMessage, errorCode);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertEquals(errorCode, response.getErrorCode());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testBuilderPattern() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .success(true)
                .message("Custom message")
                .data(42)
                .timestamp(now)
                .build();

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Custom message", response.getMessage());
        assertEquals(42, response.getData());
        assertEquals(now, response.getTimestamp());
        assertNull(response.getErrorCode());
    }

    @Test
    void testTimestampIsRecent() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        ApiResponse<String> response = ApiResponse.success("data");
        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isAfter(beforeCreation) ||
                   response.getTimestamp().isEqual(beforeCreation));
        assertTrue(response.getTimestamp().isBefore(afterCreation) ||
                   response.getTimestamp().isEqual(afterCreation));
    }

    @Test
    void testSuccessWithNullData() {
        // When
        ApiResponse<String> response = ApiResponse.success(null);

        // Then
        assertTrue(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testErrorWithNullMessage() {
        // When
        ApiResponse<String> response = ApiResponse.error(null, "ERROR_CODE");

        // Then
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertEquals("ERROR_CODE", response.getErrorCode());
    }
}
