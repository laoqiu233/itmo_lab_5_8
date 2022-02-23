package com.dmtri.common.exceptions;

public class IncorrectFileStructureException extends Exception {
    public IncorrectFileStructureException(String msg) {
        super(msg);
    }

    public IncorrectFileStructureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
