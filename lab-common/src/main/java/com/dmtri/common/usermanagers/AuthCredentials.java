package com.dmtri.common.usermanagers;

import java.io.Serializable;

public class AuthCredentials implements Serializable {
    private static final long serialVersionUID = 9130887391730028055L;
    private final String login;
    private final String password;

    public AuthCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
