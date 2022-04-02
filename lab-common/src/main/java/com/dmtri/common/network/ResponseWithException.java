package com.dmtri.common.network;

public class ResponseWithException extends Response {
    private static final long serialVersionUID = -3314926346257037028L;

    private Exception e;

    public ResponseWithException(Exception e) {
        super("The server responded with an exception");
        this.e = e;
    }

    public Exception getException() {
        return e;
    }
}
