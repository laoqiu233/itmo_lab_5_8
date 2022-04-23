package com.dmtri.common.exceptions;

public class UnauthenticatedException extends Exception {
    public UnauthenticatedException() {
        super("You are unauthenticated, please login to execute commands");
    }

    public UnauthenticatedException(String message) {
        super(message);
    }

    public UnauthenticatedException(Throwable cause) {
        super(cause);
    }

    public UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthenticatedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
