package com.dmtri.server.usermanagers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Base64.Encoder;

import com.dmtri.common.usermanagers.AuthCredentials;
import com.dmtri.common.usermanagers.UserManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUserManager implements UserManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlUserManager.class);
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS users ("
                                                   + "    id serial PRIMARY KEY,"
                                                   + "    login varchar(50) NOT NULL UNIQUE,"
                                                   + "    password varchar(30) NOT NULL,"
                                                   + "    salt varchar(8) NOT NULL)";
    private final Connection conn;

    public SqlUserManager(Connection conn) throws SQLException {
        this.conn = conn;

        try (Statement s = conn.createStatement()) {
            s.execute(CREATE_TABLE_QUERY);
        }
    }

    private static String encodeHashWithSalt(String message, String salt) throws NoSuchAlgorithmException {
        Encoder encoder = Base64.getEncoder();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest((message + salt).getBytes(StandardCharsets.UTF_8));
        return encoder.encodeToString(hash);
    }

    @Override
    public Long authenticate(AuthCredentials auth) {
        final String findUserQuery = "SELECT id, password, salt FROM users WHERE login = ? LIMIT 1";

        if (auth == null) {
            return null;
        }

        try (PreparedStatement s = conn.prepareStatement(findUserQuery)) {
            s.setString(1, auth.getLogin());
            try (ResultSet res = s.executeQuery()) {
                if (res.next()) {
                    String realPasswordHashed = res.getString("password");
                    String passwordHashed = encodeHashWithSalt(auth.getPassword(), res.getString("salt"));

                    if (passwordHashed.equals(realPasswordHashed)) {
                        return res.getLong("id");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to authenticate user because of SQL exception", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to find hashing algorithm while logging in user", e);
        }

        return null;
    }

    @Override
    public Long register(AuthCredentials auth) {
        final String registerQuery = "INSERT INTO users VALUES (default, ?, ?, ?) RETURNING id;";
        final int loginIndex = 1;
        final int passwordIndex = 2;
        final int saltIndex = 3;
        final int saltBytes = 6;

        try (PreparedStatement s = conn.prepareStatement(registerQuery)) {
            Encoder encoder = Base64.getEncoder();
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[saltBytes];
            random.nextBytes(salt);
            String saltStr = encoder.encodeToString(salt);

            String hashStr = encodeHashWithSalt(auth.getPassword(), saltStr);

            s.setString(loginIndex, auth.getLogin());
            s.setString(passwordIndex, hashStr);
            s.setString(saltIndex, saltStr);

            try (ResultSet res = s.executeQuery()) {
                res.next();
                return res.getLong("id");
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to find hashing algorithm while registering user", e);
            return null;
        } catch (SQLException e) {
            LOGGER.error("Failed to register user", e);
            return null;
        }
    }

    @Override
    public String getUsernameById(long userId) {
        final String findUserQuery = "SELECT login FROM users WHERE id = ? LIMIT 1";

        try (PreparedStatement s = conn.prepareStatement(findUserQuery)) {
            s.setLong(1, userId);
            try (ResultSet res = s.executeQuery()) {
                if (res.next()) {
                    return res.getString("login");
                }

                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to find username by id", e);
            return null;
        }
    }
}
