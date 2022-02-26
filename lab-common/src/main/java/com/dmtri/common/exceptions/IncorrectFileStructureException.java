package com.dmtri.common.exceptions;

public class IncorrectFileStructureException extends Exception {
    public IncorrectFileStructureException() {
    }

    public IncorrectFileStructureException(String message) {
        super(message);
    }

    public IncorrectFileStructureException(Throwable cause) {
        super(cause);
    }

    public IncorrectFileStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectFileStructureException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
