package com.dmtri.common.network;

import java.io.Serializable;

import com.dmtri.common.usermanagers.AuthCredentials;

public class Request implements Serializable {
    private static final long serialVersionUID = 4643797287519810631L;
    private String commandName;
    private AuthCredentials auth;
    private RequestBody body;

    public Request(String commandName, RequestBody body, AuthCredentials auth) {
        this.commandName = commandName;
        this.body = body;
        this.auth = auth;
    }

    public String getCommandName() {
        return commandName;
    }

    public RequestBody getBody() {
        return body;
    }

    public AuthCredentials getAuth() {
        return auth;
    }
}
