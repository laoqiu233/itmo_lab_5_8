package com.dmtri.server;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.collectionmanagers.FileCollectionManager;
import com.dmtri.common.exceptions.IncorrectFileStructureException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

public final class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String fileName = System.getenv("FILENAME");
        int port;

        if (fileName == null) {
            LOGGER.fatal("ERROR: The collection file should be specified in environment variables with the name FILENAME");
            System.exit(1);
            return;
        }

        try {
            port = Integer.valueOf(args[0]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            LOGGER.fatal("Invalid port provided. The port number should be entered as the first argument", e);
            System.exit(1);
            return;
        }

        CollectionManager cm;

        try {
            cm = new FileCollectionManager(fileName);
        } catch (
            SAXException
            | IOException
            | IncorrectFileStructureException
            | ParserConfigurationException e
        ) {
            LOGGER.fatal("Failed to parse provided file \"" + fileName + "\"", e);
            System.exit(1);
            return;
        }

        ServerInstance server = new ServerInstance(port, cm);
        server.run();
    }
}
