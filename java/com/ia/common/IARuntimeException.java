package com.ia.common;

import static com.ia.log.LogUtil.getLogger;

import com.ia.log.Logger;

public class IARuntimeException extends RuntimeException {
    private static final long serialVersionUID = 6998992197504832498L;

    private final Logger logger = getLogger(this.getClass());

    private String errorCode = "-1";

    public IARuntimeException() {
        super();
    }

    public IARuntimeException(final String msg) {
        super(msg);
        logger.error(msg);
    }

    public IARuntimeException(final String msg, final Exception e) {
        super(msg, e);
        logger.error(msg, e);
    }

    public IARuntimeException(final String msg, final String errorCode) {
        super(msg);
        logger.error(msg);
        this.errorCode = errorCode;
    }

    public IARuntimeException(final String msg, final String errorCode, final Exception e) {
        super(msg, e);
        logger.error(msg, e);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
