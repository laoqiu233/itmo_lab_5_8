package com.dmtri.common.network;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 299712538080526778L;
    private String message = "";
    private String localeKey = null;
    private Object[] params = new Object[] {};

    public Response() {
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, String localeKey, Object[] params) {
        this(message);
        this.localeKey = localeKey;
        this.params = params;
    }

    public String getLocaleKey() {
        return localeKey;
    }

    public Object[] getParams() {
        return params;
    }

    public String getMessage() {
        return message;
    }
}
