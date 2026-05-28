package br.com.nutrimind.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateUtil {
    private DateUtil() {
    }

    public static String dateTime(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    public static LocalDateTime parseDateTime(String value) {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
    }

    public static String date(LocalDate value) {
        return value == null ? null : value.toString();
    }

    public static LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}

