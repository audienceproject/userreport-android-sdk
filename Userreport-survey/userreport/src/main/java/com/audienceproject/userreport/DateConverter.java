package com.audienceproject.userreport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateConverter {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    private DateConverter() {
    }

    public static Date getCurrentDate() {
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        return c.getTime();
    }

    public static Date addDays(int daysCount) {
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        c.add(Calendar.DATE, daysCount);

        return c.getTime();
    }

    public static Date convert(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException ignored) {
        }
        return null;
    }

    public static String asString(Date date) {
        return DATE_FORMAT.format(date);
    }
}
