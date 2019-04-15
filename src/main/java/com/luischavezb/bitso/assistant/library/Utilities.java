package com.luischavezb.bitso.assistant.library;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class Utilities {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static Date parseDateTime(Object object) {
        String date = object.toString();

        if (date.contains("+")) {
            date = date.substring(0, date.indexOf("+"));
        }

        try {
            return DATE_TIME_FORMATTER.parseDateTime(date).toDate();
        } catch (Exception ex) {
            // IGNORE
        }

        return null;
    }

    public static Date parseDate(Object object) {
        try {
            return DATE_FORMATTER.parseDateTime(object.toString()).toDate();
        } catch (Exception ex) {
            // IGNORE
        }

        return null;
    }

    public static String formatDateTime(Date date) {
        return new DateTime(date).toString(DATE_TIME_FORMATTER);
    }

    public static String formatTime(Date date) {
        return new DateTime(date).toString(TIME_FORMATTER);
    }

    public static String formatDate(Date date) {
        return new DateTime(date).toString(DATE_FORMATTER);
    }
}
