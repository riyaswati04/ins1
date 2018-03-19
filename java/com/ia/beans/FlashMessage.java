package com.ia.beans;

import static com.ia.beans.FlashMessage.MESSAGE_LEVEL.ERROR;
import static com.ia.beans.FlashMessage.MESSAGE_LEVEL.INFO;
import static com.ia.beans.FlashMessage.MESSAGE_LEVEL.SUCCESS;
import static com.ia.beans.FlashMessage.MESSAGE_LEVEL.WARN;

public class FlashMessage {
    public static enum MESSAGE_LEVEL {
        ERROR, INFO, SUCCESS, WARN
    }

    public static FlashMessage create(final MESSAGE_LEVEL messageLevel, final String message) {
        return new FlashMessage(messageLevel, message);
    }

    public static FlashMessage error(final String message) {
        return create(ERROR, message);
    }

    public static FlashMessage info(final String message) {
        return create(INFO, message);
    }

    public static FlashMessage success(final String message) {
        return create(SUCCESS, message);
    }

    public static FlashMessage warn(final String message) {
        return create(WARN, message);
    }

    private final String message;

    private final MESSAGE_LEVEL messageLevel;

    private FlashMessage(final MESSAGE_LEVEL messageLevel, final String message) {
        super();
        this.messageLevel = messageLevel;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public MESSAGE_LEVEL getMessageLevel() {
        return messageLevel;
    }

    public boolean isSuccess() {
        return SUCCESS.equals(getMessageLevel());
    }
}
