package com.enterprise.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public final class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    private DateTimeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(UTC_ZONE);
    }

    public static String formatToIso(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_FORMATTER);
    }

    public static LocalDateTime parseFromIso(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, ISO_FORMATTER);
    }
}
