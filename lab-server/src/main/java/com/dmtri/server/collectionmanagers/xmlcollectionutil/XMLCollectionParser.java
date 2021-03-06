package com.dmtri.server.collectionmanagers.xmlcollectionutil;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public final class XMLCollectionParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLCollectionParser.class);

    private XMLCollectionParser() {
    }

    private static Document openDocument(String fileName) throws IOException, ParserConfigurationException {
        // Initialize XML parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Чтение данных из файла необходимо реализовать с помощью класса java.io.InputStreamReader
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName))) {
            StringBuilder sb = new StringBuilder();
            int c;

            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }

            reader.close();

            Document doc = db.parse(new ByteArrayInputStream(sb.toString().getBytes()));

            return doc;
        } catch (SAXException | FileNotFoundException e) {
            LOGGER.warn("The file is in a invalid format or does not exist, original file will be rewritten when saved.");
            return db.newDocument();
        }
    }

    private static Element getDocumentRoot(Document doc) throws IncorrectFileStructureException {
        Element root = doc.getDocumentElement();
        if (root == null) {
            root = doc.createElement("routes");
            root.setAttribute("nextId", "1");
        }

        if (!root.getTagName().equals("routes")) {
            throw new IncorrectFileStructureException(
                "Corrupted file: Root element should be \"routes\""
            );
        }

        if (root.getAttribute("nextId").isEmpty()) {
            throw new IncorrectFileStructureException(
                "Root node should have a nextId attribute defiend"
            );
        }

        return root;
    }

    private static void handleRouteElement(Element el, List<Route> collection) {
        try {
            Route route = XMLRouteParser.parseRoute(el);
            // Check id uniqueness
            if (collection.stream().anyMatch(x -> x.getId() == route.getId())) {
                throw new InvalidFieldException("The id " + route.getId() + " is already used by another route");
            }
            collection.add(route);
        } catch (InvalidFieldException e) {
            LOGGER.warn("Caught exception while parsing route, skipping invalid route...", e);
        }
    }

    public static ParsedCollection parse(String fileName) throws IncorrectFileStructureException, IOException, ParserConfigurationException  {
        // Open document and get root
        Element root = getDocumentRoot(openDocument(fileName));

        ParsedCollection parsed = new ParsedCollection();
        try {
            parsed.setNextId(Long.parseLong(root.getAttribute("nextId")));
        } catch (NumberFormatException e) {
            throw new IncorrectFileStructureException("Invalid value for nextId", e);
        }

        LOGGER.info("Parsing routes in file " + fileName);
        NodeList nodes = root.getElementsByTagName("Route");
        List<Route> collection = new LinkedList<Route>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            handleRouteElement(el, collection);
        }

        LOGGER.info("Parsing finished. Total routes retrieved from file: " + collection.size());
        parsed.setCollection(collection);
        return parsed;
    }

    public static class ParsedCollection {
        private Long nextId;
        private List<Route> collection;

        public void setNextId(Long nextId) {
            this.nextId = nextId;
        }

        public void setCollection(List<Route> collection) {
            this.collection = collection;
        }

        public Long getNextId() {
            return nextId;
        }

        public List<Route> getCollection() {
            return collection;
        }
    }
}
