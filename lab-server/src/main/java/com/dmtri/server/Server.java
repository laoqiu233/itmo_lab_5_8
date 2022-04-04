package com.dmtri.server;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.collectionmanagers.FileCollectionManager;
import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.util.TerminalColors;

import org.xml.sax.SAXException;

public final class Server {
    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String fileName = System.getenv("FILENAME");

        if (fileName == null) {
            System.err.println(TerminalColors.colorString("ERROR: The collection file should be specified in environment variables with the name FILENAME", TerminalColors.RED));
            return;
        }

        CollectionManager cm;

        try {
            cm = new FileCollectionManager(fileName);
        } catch (
            SAXException
            | IOException
            | IncorrectFileStructureException
            | ParserConfigurationException
            | TransformerException e
        ) {
            System.out.println(TerminalColors.colorString("Failed to parse provided file \"" + fileName + "\"", TerminalColors.RED));
            System.out.println(e);
            System.exit(1);
            return;
        }

        ServerInstance server = new ServerInstance(Integer.valueOf(args[0]), cm);
        server.run();
    }
}
