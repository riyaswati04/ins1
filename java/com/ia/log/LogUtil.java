package com.ia.log;

import static com.ia.common.Handlers.logger;

public final class LogUtil {
    private static final class PrefixMessageLogger implements Logger {
        private final String logMessagePrefix;

        private PrefixMessageLogger(final String logMessagePrefix) {
            this.logMessagePrefix = logMessagePrefix;
        }

        @Override
        public void debug(final String template, final Object... arguments) {
            logger.debug(format(logMessagePrefix, template, arguments));

        }

        @Override
        public void debug(final Throwable e, final String template, final Object... arguments) {
            logger.debug(format(logMessagePrefix, template, arguments), e);

        }

        @Override
        public void error(final String template, final Object... arguments) {
            logger.error(format(logMessagePrefix, template, arguments));

        }

        @Override
        public void error(final Throwable e, final String template, final Object... arguments) {
            logger.error(format(logMessagePrefix, template, arguments), e);

        }

        @Override
        public void exception(final String template, final Throwable e, final Object... arguments) {
            error(e, template, arguments);
        }

        private String format(final String logMessagePrefix, final String template,
                final Object... arguments) {
            return '[' + logMessagePrefix + "] " + String.format(template, arguments);
        }

        @Override
        public void info(final String template, final Object... arguments) {
            logger.info(format(logMessagePrefix, template, arguments));

        }

        @Override
        public void info(final Throwable e, final String template, final Object... arguments) {
            logger.info(format(logMessagePrefix, template, arguments), e);

        }

        @Override
        public void warn(final String template, final Object... arguments) {
            logger.warn(format(logMessagePrefix, template, arguments));

        }

        @Override
        public void warn(final Throwable e, final String template, final Object... arguments) {
            logger.warn(format(logMessagePrefix, template, arguments), e);

        }
    }

    public static Logger getLogger(final Class<?> klass) {
        final String logMessagePrefix = klass.getSimpleName();
        return new PrefixMessageLogger(logMessagePrefix);
    }
}
