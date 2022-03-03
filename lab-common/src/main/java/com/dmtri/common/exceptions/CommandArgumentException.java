package com.dmtri.common.exceptions;

public class CommandArgumentException extends Exception {
    public CommandArgumentException() {
    }

    public CommandArgumentException(String message) {
        super(message);
    }

    public CommandArgumentException(Throwable cause) {
        super(cause);
    }

    public CommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandArgumentException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CommandArgumentException(String commandName, int need, int recieved) {
        super(commandName + " takes exactly " + need + " arguments. Recieved " + recieved);
    }

    public CommandArgumentException(String commandName, int recieved) {
        super(commandName + " takes no arguments. Recieved " + recieved);
    }
}
