package com.dmtri.common.exceptions;

public class InvalidFieldException extends Exception {
    private String localeKey = null;

    public InvalidFieldException() {
    }

    public InvalidFieldException(String message) {
        super(message);
    }

    public InvalidFieldException(String message, String localeKey) {
        super(message);
        this.localeKey = localeKey;
    }

    public InvalidFieldException(Throwable cause) {
        super(cause);
    }

    public InvalidFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFieldException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getLocaleKey() {
        return localeKey;
    }
}
