package com.dmtri.client.collectionmanagers.xmlcollectionutil;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

public final class XMLCollectionParser {
    private XMLCollectionParser() {
    }

    private static Document openDocument(String fileName) throws IncorrectFileStructureException, IOException, ParserConfigurationException, SAXException {
        // Initialize XML parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Чтение данных из файла необходимо реализовать с помощью класса java.io.InputStreamReader
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName));) {
            StringBuilder sb = new StringBuilder();
            int c;

            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }

            reader.close();

            Document doc = db.parse(new ByteArrayInputStream(sb.toString().getBytes()));

            return doc;
        }
    }

    private static Element getDocumentRoot(Document doc) throws IncorrectFileStructureException {
        Element root = doc.getDocumentElement();

        if (root.getTagName().equals("routes")) {
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

    private static void handleRouteElement(Element el, List<Route> collection) throws TransformerException {
        try {
            Route route = XMLRouteParser.parseRoute(el);
            // Check id uniqueness
            if (collection.stream().anyMatch(x -> x.getId() == route.getId())) {
                throw new InvalidFieldException("The id " + route.getId() + " is already used by another route");
            }
            collection.add(route);
        } catch (InvalidFieldException e) {
            System.out.println("Caught exception while parsing route: ");
            XMLCollectionWriter.writeElementToConsole(el);
            System.out.println(e);
            if (e.getCause() != null) {
                System.out.println(e.getCause());
            }
            System.out.println("Skipping invalid route...");
        }
    }

    public static ParsedCollection parse(String fileName) throws IncorrectFileStructureException, IOException, ParserConfigurationException, TransformerException, SAXException  {
        // Open document and get root
        Element root = getDocumentRoot(openDocument(fileName));

        ParsedCollection parsed = new ParsedCollection();
        try {
            parsed.setNextId(Long.parseLong(root.getAttribute("nextId")));
        } catch (NumberFormatException e) {
            throw new IncorrectFileStructureException("Invalid value for nextId", e);
        }

        System.out.println("Parsing routes in file " + fileName);
        NodeList nodes = root.getElementsByTagName("Route");
        List<Route> collection = new LinkedList<Route>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            handleRouteElement(el, collection);
        }

        System.out.println("Parsing finished. Total routes retrieved from file: " + collection.size());
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
