package net.java.cargotracker.application.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A few utils for working with Date.
 */
// TODO Make this a CDI singleton?
public class DateUtil {

    public static Date toDate(String date) {
        return toDate(date, "00:00.00.000");
    }

    public static Date toDate(String date, String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date + " " + time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private DateUtil() {
    }
}
