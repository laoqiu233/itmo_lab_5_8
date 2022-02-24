package com.dmtri.common.exceptions;

public class InvalidFieldException extends Exception {
    public InvalidFieldException() {
    }

    public InvalidFieldException(String message) {
        super(message);
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
}
