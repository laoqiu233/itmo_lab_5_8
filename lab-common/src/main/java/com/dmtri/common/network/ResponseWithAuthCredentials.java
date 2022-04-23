package com.dmtri.common.network;

import com.dmtri.common.usermanagers.AuthCredentials;

public class ResponseWithAuthCredentials extends Response {
    private static final long serialVersionUID = 5427217733757349628L;
    private final AuthCredentials auth;

    public ResponseWithAuthCredentials(AuthCredentials auth) {
        super("Successfully authenticated");
        this.auth = auth;
    }

    public ResponseWithAuthCredentials(AuthCredentials auth, String message) {
        super(message);
        this.auth = auth;
    }

    public AuthCredentials getAuthCredentials() {
        return auth;
    }
}
