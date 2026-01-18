package com.enterprise.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all microservices.
 *
 * <p>This class provides a consistent response structure across all services in the
 * enterprise microservices ecosystem. It wraps the actual response data along with
 * metadata such as success status, optional messages, error codes, and timestamps.</p>
 *
 * <p>The response follows a standardized format that allows clients to easily parse
 * responses and handle both successful and error cases uniformly.</p>
 *
 * <p><strong>Example successful response:</strong></p>
 * <pre>{@code
 * {
 *   "success": true,
 *   "message": "User retrieved successfully",
 *   "data": { "id": 1, "name": "John Doe" },
 *   "timestamp": "2026-01-01T12:00:00"
 * }
 * }</pre>
 *
 * <p><strong>Example error response:</strong></p>
 * <pre>{@code
 * {
 *   "success": false,
 *   "message": "User not found",
 *   "errorCode": "RESOURCE_NOT_FOUND",
 *   "timestamp": "2026-01-01T12:00:00"
 * }
 * }</pre>
 *
 * @param <T> the type of the data being returned in the response
 * @author Enterprise Team
 * @version 1.0.0
 * @since 2026-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse_ENHANCED<T> {

    /** Indicates whether the operation was successful */
    private boolean success;

    /** Optional human-readable message describing the result */
    private String message;

    /** The actual response data (null in case of errors) */
    private T data;

    /** Machine-readable error code (only present in error responses) */
    private String errorCode;

    /** Timestamp when the response was generated */
    private LocalDateTime timestamp;

    /**
     * Creates a successful API response with data only.
     *
     * <p>This factory method creates a response with success=true and includes
     * the provided data. The timestamp is automatically set to the current time.</p>
     *
     * @param <T> the type of the data
     * @param data the response data to include
     * @return a successful ApiResponse containing the data
     */
    public static <T> ApiResponse_ENHANCED<T> success(T data) {
        return ApiResponse_ENHANCED.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful API response with both message and data.
     *
     * <p>This factory method creates a response with success=true, includes both
     * a descriptive message and the response data. The timestamp is automatically
     * set to the current time.</p>
     *
     * @param <T> the type of the data
     * @param message a human-readable success message
     * @param data the response data to include
     * @return a successful ApiResponse containing the message and data
     */
    public static <T> ApiResponse_ENHANCED<T> success(String message, T data) {
        return ApiResponse_ENHANCED.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error API response with message and error code.
     *
     * <p>This factory method creates a response with success=false, includes
     * both an error message and a machine-readable error code. The timestamp
     * is automatically set to the current time.</p>
     *
     * <p><strong>Common error codes:</strong></p>
     * <ul>
     *   <li>RESOURCE_NOT_FOUND - requested resource doesn't exist</li>
     *   <li>BUSINESS_ERROR - business rule validation failed</li>
     *   <li>INVALID_AMOUNT - numeric validation failed</li>
     *   <li>INTERNAL_ERROR - unexpected server error</li>
     * </ul>
     *
     * @param <T> the type of the data (will be null in error responses)
     * @param message a human-readable error message
     * @param errorCode a machine-readable error code for client handling
     * @return an error ApiResponse containing the error details
     */
    public static <T> ApiResponse_ENHANCED<T> error(String message, String errorCode) {
        return ApiResponse_ENHANCED.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
