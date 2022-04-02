package com.dmtri.common.network;

import java.io.Serializable;

public class RequestBody implements Serializable {
    private static final long serialVersionUID = -5827092870610437678L;
    private String[] args;

    public RequestBody(String[] args) {
        this.args = args;
    }

    public String getArg(int i) {
        return args[i];
    }

    public int getArgsLength() {
        return args.length;
    }
}
