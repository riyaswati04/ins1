package com.ia.exception;

import static com.ia.log.LogUtil.getLogger;

import com.ia.log.Logger;

public class IACommonException extends Exception {

    private static final long serialVersionUID = 6998992197504832789L;

    private final Logger logger = getLogger(getClass());

    private String errorCode = null;

    public IACommonException(final Exception e) {
        super(e);
    }

    public IACommonException(final String msg) {
        super(msg);
        logger.error(msg);
    }

    public IACommonException(final String msg, final Exception e) {
        super(msg, e);
        logger.error(msg, e);
    }

    public IACommonException(final String msg, final String errorCode) {
        super(msg);
        logger.error(msg);
        this.errorCode = errorCode;
    }

    public IACommonException(final String msg, final String errorCode, final Exception e) {
        super(msg, e);
        logger.error(msg, e);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
