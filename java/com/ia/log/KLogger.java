package com.ia.log;

import static com.ia.common.IAProperties.logDirProp;
import static com.ia.common.IAProperties.logFileProp;
import static com.ia.common.IAProperties.servletDebug;
import static com.ia.util.DateUtil.MYSQL_DATE_FORMAT;
import static com.ia.util.DateUtil.PERFIOS_DATE_FORMAT;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.google.inject.Inject;
import com.ia.services.MailerService;
import com.ia.util.DateUtil;

/**
 * This class is responsible for logging all ia messages.
 *
 * The properties log.dir and log.file are used to determine the location and name of the log file.
 *
 * The actual log file name uses the date stamp in dd-MM-yyyy format.
 *
 * For example, if log.file=ia.log or log.file=ia, then the log file for 27th May 2008 will have the
 * name ia.27-05-2008.log.
 *
 * Automatic switchover to next date happens at 12 PM midnight.
 *
 * If the file can't be opened for writing for any reason, standard error output (stderr) gets used.
 */
public final class KLogger {

    private static final String DEBUG = "DEBUG";

    private static final String ERROR = "ERROR";

    private static final String INFO = "INFO";

    private static final String LOG_MESSAGE_TEMPLATE = "[%s] %s";

    private static Timer logFlusher;

    private static StringBuilder messages = new StringBuilder();

    private static PrintWriter stderr = new PrintWriter(System.err, true);

    private static PrintWriter out = stderr;

    private static boolean stderrUsed = true;

    public static boolean toDebug;

    private static final String WARN = "WARN";

    private String logFileDate; // date string in dd-MM-yyyy format

    private long prevDays; // number of days since 01-01-1970

    private final int tzOffset; // Time zone offset (for IST, 5:30 hrs in millis)

    private final MailerService mailer;

    @Inject
    public KLogger(final MailerService mailer) {
        tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
        toDebug = servletDebug;
        this.mailer = mailer;
    }

    private void closeLog() {
        if (!stderrUsed) {
            out.close();
        }
        stderrUsed = true;
        out = stderr;
    }

    public void debug(final String message) {
        debug(message, null);
    }

    // Log debug messages ONLY if servletDebug is set.
    public void debug(final String message, final Throwable e) {
        if (servletDebug) {
            handleMessage(DEBUG, message, e);
        }
    }

    public void error(final String message) {
        handleMessage(ERROR, message, null);
    }

    public void error(final String prefix, final String template, final Object... args) {
        handleMessage(ERROR, format(LOG_MESSAGE_TEMPLATE, prefix, format(template, args)), null);
    }

    public void error(final String message, final Throwable e) {
        handleMessage(ERROR, message, e);
    }

    public void exception(final String template, final Throwable e, final Object... arguments) {
        error(format(template, arguments), e);
    }

    private synchronized void flushLog() {
        String str = null;
        synchronized (messages) {
            if (messages.length() == 0) {
                return;
            }
            str = messages.toString();
            messages.setLength(0);
        }
        out.print(str);
        out.flush();
    }

    /**
     * Format: tag threadID hh:mm:ss message
     */
    private void handleMessage(final String tag, final String message, final Throwable e) {

        String stackTrace = null;

        synchronized (messages) {
            final long timeInSecs = (currentTimeMillis() + tzOffset) / 1000;
            final long days = timeInSecs / (24 * 60 * 60);
            int secs = (int) (timeInSecs - days * (24 * 60 * 60));
            final int hrs = secs / (60 * 60);
            secs -= hrs * 60 * 60;
            final int mins = secs / 60;
            secs -= mins * 60;

            // Check if day has changed
            if (days > prevDays) {
                out.print(messages.toString());
                messages.setLength(0);
                closeLog();
                logFileDate = PERFIOS_DATE_FORMAT.format(new Date());
                DateUtil.TODAY = MYSQL_DATE_FORMAT.format(new Date());
                openLog();
            }
            messages.append(tag).append('\t').append(currentThread().getId()).append('\t')
                    .append(logFileDate).append('\t');
            if (hrs < 10) {
                messages.append('0');
            }
            messages.append(hrs).append(':');
            if (mins < 10) {
                messages.append('0');
            }
            messages.append(mins).append(':');
            if (secs < 10) {
                messages.append('0');
            }
            messages.append(secs).append('\t').append(message);
            if (e == null) {
                messages.append('\n');
            }
            else {
                final StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                stackTrace = sw.toString();
                messages.append(" : ").append(e.toString()).append('\n');
                messages.append(stackTrace);
                mailer.sendMailToSupport(message + stackTrace, "Exception ");
            }
        }
    }

    public void info(final String message) {
        handleMessage(INFO, message, null);
    }

    public void info(final String prefix, final String template, final Object... args) {
        handleMessage(INFO, format(LOG_MESSAGE_TEMPLATE, prefix, format(template, args)), null);
    }

    public void info(final String message, final Throwable e) {
        handleMessage(INFO, message, e);
    }

    private void openLog() {
        String logFileName;
        if (logFileDate == null) {
            logFileDate = PERFIOS_DATE_FORMAT.format(new Date());
            prevDays = (currentTimeMillis() + tzOffset) / (24 * 60 * 60 * 1000);
        }
        final int index = logFileProp.lastIndexOf('.');
        if (index != -1) {
            logFileName = logFileProp.substring(0, index + 1) + logFileDate
                    + logFileProp.substring(index);
        }
        else {
            logFileName = logFileProp + '.' + logFileDate + ".log";
        }
        final File logFile = new File(logDirProp, logFileName);
        // Open the logFile in append and auto-flush mode
        try {
            out = new PrintWriter(new FileWriter(logFile, true), true);
            stderrUsed = false;
        }
        catch (final IOException e) {
            // can't open logFile for writing -- use stderr instead
            out = stderr;
            out.println("Can't open logfile " + logFile + " for writing -- using stderr instead");
            stderrUsed = true;
        }
    }

    public void shutdown() {
        info("------- Shutting down Logger ----------------------");
        logFlusher.cancel();
        out.print(messages.toString());
        closeLog();
    }

    public void start() {
        // start() is called by the servlet at startup or after a shutdown.

        // logger is opened/closed as per start()/shutdown()
        openLog();
        // start the logFlusher
        logFlusher = new Timer("JobServer-Logger", true); // daemon process
        logFlusher.schedule(new TimerTask() {
            @Override
            public void run() {
                flushLog();
            }
        }, 0L, 3 * 1000L); // every 3 seconds
        info("------- Starting Logging ----------------------");
    }

    public void warn(final String message) {
        handleMessage(WARN, message, null);
    }

    public void warn(final String prefix, final String template, final Object... args) {
        handleMessage(WARN, format(LOG_MESSAGE_TEMPLATE, prefix, format(template, args)), null);
    }

    public void warn(final String message, final Throwable e) {
        handleMessage(WARN, message, e);
    }
}
