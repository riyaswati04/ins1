package com.ia.util;

import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeFieldType.monthOfYear;
import static org.joda.time.DateTimeFieldType.year;
import static org.joda.time.format.DateTimeFormat.forPattern;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public final class DateUtil {

    public static final ThreadSafeSimpleDateFormat MYSQL_DATE_FORMAT =
            new ThreadSafeSimpleDateFormat("yyyy-MM-dd");

    public static final ThreadSafeSimpleDateFormat PERFIOS_DATE_FORMAT =
            new ThreadSafeSimpleDateFormat("dd-MM-yyyy");

    public static final ThreadSafeSimpleDateFormat TIMESTAMP_FORMAT =
            new ThreadSafeSimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String TODAY = MYSQL_DATE_FORMAT.format(new Date());

    public static final ThreadSafeSimpleDateFormat MYSQL_TIMESTAMP_FORMAT =
            new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final ThreadSafeSimpleDateFormat TIME_FORMAT =
            new ThreadSafeSimpleDateFormat("HH:mm:ss");

    public static String TODAY_TS = MYSQL_TIMESTAMP_FORMAT.format(new Date());

    private static final DateTimeFormatter FORMAT_USER_FRIENDLY_DATE = forPattern("dd-MMM-yyyy");

    public static String INCEPTION_DATE = "1971-01-01";

    private static final DateTimeFormatter MYSQL_DATE_INPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendFixedDecimal(year(), 4).appendLiteral('-').appendFixedDecimal(monthOfYear(), 2)
            .appendLiteral('-').appendFixedDecimal(dayOfMonth(), 2).toFormatter();

    public static Date convert(final String date) throws ParseException {
        final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        final Date d = s.parse(date);
        return d;
    }

    public static int daysBetween(final Date dateA, final Date dateB) {
        return new Long((dateB.getTime() - dateA.getTime()) / 86400000).intValue();
    }

    /**
     * Given two dates in milliseconds, return the days between those two
     */
    public static int daysBetween(final long millisecondA, final long milliecondB) {
        return new Long((milliecondB - millisecondA) / 86400000).intValue();
    }

    public static String formatTime(final String date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date convert = sdf.parse(date);
        final String newDateString = sdf.format(convert);
        return newDateString;
    }

    /**
     * Given two dates in milliseconds, return the hours between those two
     */
    public static int hoursBetween(final long millisecondA, final long milliecondB) {
        return new Long((milliecondB - millisecondA) / 3600000).intValue();
    }

    public static boolean isValidMYSQLDate(final String input) {
        boolean isValid = false;

        try {
            isValid = null != MYSQL_DATE_INPUT_FORMAT.parseLocalDate(input);

        }
        catch (final IllegalArgumentException ignore) {
        }

        return isValid;
    }

    /**
     * Given two dates in milliseconds, return the minutes between those two
     */
    public static int minutesBetween(final long millisecondA, final long milliecondB) {
        return new Long((milliecondB - millisecondA) / 60000).intValue();
    }

    public static Timestamp parse(final String date) throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date convert = dateFormat.parse(date);
        final Timestamp timestamp = new Timestamp(convert.getTime());
        return timestamp;
    }

    /**
     * Given two dates in milliseconds, return the seconds between those two
     */
    public static int secondsBetween(final long millisecondA, final long milliecondB) {
        return new Long((milliecondB - millisecondA) / 1000).intValue();
    }

    public static String toUserFriendlyDateFormat(final LocalDate dateTime) {
        return FORMAT_USER_FRIENDLY_DATE.print(dateTime);
    }

}
