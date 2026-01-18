package com.enterprise.common.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DateTimeUtil}.
 *
 * @author Enterprise Team
 * @version 1.0.0
 * @since 2026-01-01
 */
class DateTimeUtilTest {

    @Test
    void testNowReturnsUtcTime() {
        // When
        LocalDateTime result = DateTimeUtil.now();

        // Then
        assertNotNull(result);
        LocalDateTime expectedUtc = LocalDateTime.now(ZoneId.of("UTC"));
        // Allow 1 second difference for test execution time
        assertTrue(result.isAfter(expectedUtc.minusSeconds(1)));
        assertTrue(result.isBefore(expectedUtc.plusSeconds(1)));
    }

    @Test
    void testFormatToIso() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 15, 10, 30, 45);

        // When
        String result = DateTimeUtil.formatToIso(dateTime);

        // Then
        assertNotNull(result);
        assertEquals("2026-01-15T10:30:45", result);
    }

    @Test
    void testFormatToIsoWithNull() {
        // When
        String result = DateTimeUtil.formatToIso(null);

        // Then
        assertNull(result);
    }

    @Test
    void testParseFromIso() {
        // Given
        String isoString = "2026-01-15T10:30:45";

        // When
        LocalDateTime result = DateTimeUtil.parseFromIso(isoString);

        // Then
        assertNotNull(result);
        assertEquals(2026, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(15, result.getDayOfMonth());
        assertEquals(10, result.getHour());
        assertEquals(30, result.getMinute());
        assertEquals(45, result.getSecond());
    }

    @Test
    void testParseFromIsoWithNull() {
        // When
        LocalDateTime result = DateTimeUtil.parseFromIso(null);

        // Then
        assertNull(result);
    }

    @Test
    void testParseFromIsoWithBlankString() {
        // When
        LocalDateTime result = DateTimeUtil.parseFromIso("   ");

        // Then
        assertNull(result);
    }

    @Test
    void testParseFromIsoWithEmptyString() {
        // When
        LocalDateTime result = DateTimeUtil.parseFromIso("");

        // Then
        assertNull(result);
    }

    @Test
    void testFormatAndParseRoundTrip() {
        // Given
        LocalDateTime original = LocalDateTime.of(2026, 12, 25, 14, 45, 30);

        // When
        String formatted = DateTimeUtil.formatToIso(original);
        LocalDateTime parsed = DateTimeUtil.parseFromIso(formatted);

        // Then
        assertEquals(original, parsed);
    }

    @Test
    void testUtilityClassCannotBeInstantiated() {
        // When/Then
        Exception exception = assertThrows(Exception.class, () -> {
            var constructor = DateTimeUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // The UnsupportedOperationException is wrapped in InvocationTargetException
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    }
}
