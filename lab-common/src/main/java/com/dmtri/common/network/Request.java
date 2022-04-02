package com.dmtri.common.network;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 4643797287519810631L;
    private String commandName;
    private RequestBody body;

    public Request(String commandName, RequestBody body) {
        this.commandName = commandName;
        this.body = body;
    }

    public String getCommandName() {
        return commandName;
    }

    public RequestBody getBody() {
        return body;
    }
}
