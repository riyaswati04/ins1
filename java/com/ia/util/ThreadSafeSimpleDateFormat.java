package com.ia.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A thread-safe implementation of the java.text.SimpleDateFormat class.
 */
public class ThreadSafeSimpleDateFormat {
    private final DateFormat df;

    public ThreadSafeSimpleDateFormat(final String format) {
        df = new SimpleDateFormat(format);
    }

    public ThreadSafeSimpleDateFormat(final String format, final boolean lenient) {
        df = new SimpleDateFormat(format);
        setLenient(lenient);
    }

    public synchronized String format(final Date date) {
        return df.format(date);
    }

    public synchronized Date parse(final String string) throws ParseException {
        return df.parse(string);
    }

    public void setLenient(final boolean lenient) {
        df.setLenient(lenient);
    }
}
