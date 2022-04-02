package com.dmtri.common.exceptions;

public class ClientSideCommandException extends RuntimeException {
    public ClientSideCommandException() {
        super("This operation is only supported on the client side");
    }

    public ClientSideCommandException(String message) {
        super(message);
    }

    public ClientSideCommandException(Throwable cause) {
        super(cause);
    }

    public ClientSideCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientSideCommandException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
