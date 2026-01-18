package com.enterprise.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ResourceNotFoundException}.
 *
 * @author Enterprise Team
 * @version 1.0.0
 * @since 2026-01-01
 */
class ResourceNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        // Given
        String resourceName = "User";
        String fieldName = "id";
        Object fieldValue = 123L;

        // When
        ResourceNotFoundException exception =
                new ResourceNotFoundException(resourceName, fieldName, fieldValue);

        // Then
        assertEquals("User not found with id: '123'", exception.getMessage());
        assertEquals(resourceName, exception.getResourceName());
        assertEquals(fieldName, exception.getFieldName());
        assertEquals(fieldValue, exception.getFieldValue());
    }

    @Test
    void testExceptionWithStringValue() {
        // Given
        String resourceName = "Order";
        String fieldName = "orderNumber";
        String fieldValue = "ORD-123";

        // When
        ResourceNotFoundException exception =
                new ResourceNotFoundException(resourceName, fieldName, fieldValue);

        // Then
        assertEquals("Order not found with orderNumber: 'ORD-123'", exception.getMessage());
        assertEquals(resourceName, exception.getResourceName());
        assertEquals(fieldName, exception.getFieldName());
        assertEquals(fieldValue, exception.getFieldValue());
    }

    @Test
    void testExceptionWithNullValue() {
        // Given
        String resourceName = "Product";
        String fieldName = "sku";
        Object fieldValue = null;

        // When
        ResourceNotFoundException exception =
                new ResourceNotFoundException(resourceName, fieldName, fieldValue);

        // Then
        assertTrue(exception.getMessage().contains("Product not found"));
        assertNull(exception.getFieldValue());
    }

    @Test
    void testExceptionIsRuntimeException() {
        // Given
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Entity", "id", 1L);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}
