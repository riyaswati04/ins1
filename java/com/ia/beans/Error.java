package com.ia.beans;

import com.ia.enums.API_ERROR_CODE;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Error")
public class Error {

    public static Error create(final API_ERROR_CODE code) {
        return create(code, code.getDefaultErrorMessage());
    }

    public static Error create(final API_ERROR_CODE code, final String message) {
        return new Error(code, message);
    }

    private final API_ERROR_CODE code;

    private final String message;

    public Error(final API_ERROR_CODE code) {
        super();
        this.code = code;
        message = code.getDefaultErrorMessage();
    }

    public Error(final API_ERROR_CODE code, final String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public API_ERROR_CODE getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error [code=" + code + ", message=" + message + "]";
    }

}
