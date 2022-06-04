package com.dmtri.common.exceptions;

public class InvalidRequestException extends Exception {
    private final String localeKey;

    public InvalidRequestException() {
        localeKey = null;
    }

    public InvalidRequestException(String message, String localeKey) {
        super(message);
        this.localeKey = localeKey;
    }

    public InvalidRequestException(Throwable cause, String localeKey) {
        super(cause);
        this.localeKey = localeKey;
    }

    public InvalidRequestException(String message, Throwable cause, String localeKey) {
        super(message, cause);
        this.localeKey = localeKey;
    }

    public InvalidRequestException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        localeKey = null;
    }

    @Override
    public String getLocalizedMessage() {
        if (localeKey == null) {
            return getMessage();
        } else {
            return localeKey;
        }
    }
}
