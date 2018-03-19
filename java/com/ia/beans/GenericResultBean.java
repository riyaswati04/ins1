package com.ia.beans;

public class GenericResultBean {

    private String message;

    private boolean success;

    public GenericResultBean() {

    }

    public GenericResultBean(final boolean success) {
        this.success = success;
    }

    public GenericResultBean(final String message, final boolean success) {
        super();
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

}
