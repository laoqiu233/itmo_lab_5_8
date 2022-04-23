package com.dmtri.common.usermanagers;

public interface UserManager {
    Long authenticate(AuthCredentials auth);
    Long register(AuthCredentials auth);
    String getUsernameById(long userId);
}
