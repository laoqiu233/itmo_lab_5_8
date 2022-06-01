package com.dmtri.common.exceptions;

public class InvalidFieldException extends Exception {
    private final String localeKey;

    public InvalidFieldException() {
        localeKey = null;
    }

    public InvalidFieldException(String message) {
        super(message);
        localeKey = null;
    }

    public InvalidFieldException(String message, String localeKey) {
        super(message);
        this.localeKey = localeKey;
    }

    public InvalidFieldException(Throwable cause) {
        super(cause);
        localeKey = null;
    }

    public InvalidFieldException(String message, Throwable cause) {
        super(message, cause);
        localeKey = null;
    }

    public InvalidFieldException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        localeKey = null;
    }

    public String getLocaleKey() {
        return localeKey;
    }
}
