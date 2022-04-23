package com.dmtri.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.server.collectionmanagers.FileCollectionManager;
import com.dmtri.server.collectionmanagers.SqlCollectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public final class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException {
        String fileName = System.getenv("FILENAME");
        String dbHost = System.getenv("DB_HOST");
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        int port;

        try {
            port = Integer.valueOf(args[0]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            LOGGER.error("Invalid port provided. The port number should be entered as the first argument", e);
            System.exit(1);
            return;
        }

        // Prioritize SQL
        if (dbHost != null && dbName != null && dbUser != null && dbPassword != null) {
            startSQLCollectionServer(port, dbHost, dbName, dbUser, dbPassword);
        } else if (fileName != null) {
            startFileCollectionServer(port, fileName);
        } else {
            LOGGER.error(
                "ERROR: You should specify a data source.\n"
                + "PSQL: DB_HOST, DB_NAME, DB_USER, DB_PASSWORD\n"
                + "File: FILENAME"
            );
            System.exit(1);
        }
    }

    private static void startSQLCollectionServer(int port, String dbHost, String dbName, String dbUser, String dbPassword) throws IOException {
        try (Connection conn = DriverManager.getConnection(
            "jdbc:postgresql://" + dbHost + '/' + dbName,
            dbUser,
            dbPassword
        )) {
            ServerInstance server = new ServerInstance(new SqlCollectionManager(conn));
            server.run(port);
        } catch (SQLException e) {
            LOGGER.error("Failed to establish postresql connection", e);
            System.exit(1);
        }
    }

    private static void startFileCollectionServer(int port, String fileName) {
        try {
            ServerInstance server = new ServerInstance(new FileCollectionManager(fileName));
            server.run(port);
        } catch (
            SAXException
            | IOException
            | IncorrectFileStructureException
            | ParserConfigurationException e
        ) {
            LOGGER.error("Failed to parse provided file \"" + fileName + "\"", e);
            System.exit(1);
        }
    }
}
