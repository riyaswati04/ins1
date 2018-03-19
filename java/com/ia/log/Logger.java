package com.ia.log;

public interface Logger {

    void debug(String template, Object... arguments);

    void debug(Throwable e, String template, Object... arguments);

    void error(String template, Object... arguments);

    void error(Throwable e, String template, Object... arguments);

    void exception(String template, Throwable e, Object... arguments);

    void info(String template, Object... arguments);

    void info(Throwable e, String template, Object... arguments);

    void warn(String template, Object... arguments);

    void warn(Throwable e, String template, Object... arguments);

}
