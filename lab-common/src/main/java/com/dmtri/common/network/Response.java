package com.dmtri.common.network;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 299712538080526778L;
    private String message = "";

    public Response() {
    }

    public Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
