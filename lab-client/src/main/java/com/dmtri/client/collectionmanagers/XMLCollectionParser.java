package com.dmtri.client.collectionmanagers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Coordinates;
import com.dmtri.common.models.Location;
import com.dmtri.common.models.Route;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

public final class XMLCollectionParser {
    private XMLCollectionParser() { 
    }

    public static class ParsedCollection {
        private Long nextId;
        private LinkedList<Route> collection;

        public void setNextId(Long nextId) {
            this.nextId = nextId;
        }

        public void setCollection(LinkedList<Route> collection) {
            this.collection = collection;
        }

        public Long getNextId() {
            return nextId;
        }

        public LinkedList<Route> getCollection() {
            return collection;
        }
    }

    public static ParsedCollection parse(String fileName) throws IncorrectFileStructureException, IOException, ParserConfigurationException, SAXException  {
        // Initialize XML parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        // Чтение данных из файла необходимо реализовать с помощью класса java.io.InputStreamReader
        InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName));
        StringBuilder sb = new StringBuilder();
        int c;

        while ((c = reader.read()) != -1) sb.append((char)c);

        reader.close();

        Document doc = db.parse(new ByteArrayInputStream(sb.toString().getBytes()));

        // Validate root node
        Element root = doc.getDocumentElement();

        if (root.getTagName() != "routes") 
            throw new IncorrectFileStructureException(
                "Corrupted file: Root element should be \"routes\""
            );

        if (root.getAttribute("nextId").isEmpty()) 
            throw new IncorrectFileStructureException(
                "Root node should have a nextId attribute defiend"
            );

        ParsedCollection parsed = new ParsedCollection();
        try {
            parsed.setNextId(Long.parseLong(root.getAttribute("nextId")));
        } catch (NumberFormatException e) {
            throw new IncorrectFileStructureException("Invalid value for nextId", e);
        }

        // Parse every node in file
        System.out.println("Parsing routes in file " + fileName);

        NodeList nodes = root.getElementsByTagName("Route");

        LinkedList<Route> collection = new LinkedList<Route>();

        for (int i=0; i<nodes.getLength(); i++) {
            Element el = (Element)nodes.item(i);

            try {
                String idStr = el.getAttribute("id");
                Long id = idStr.isEmpty() ? null : Long.parseLong(idStr);

                // Check id uniqueness
                if (collection.stream().anyMatch(x -> x.getId() == id))
                    throw new InvalidFieldException("The id " + id + " is already used by another route");
                
                String name = getElementChildValue("name", el);

                String distanceStr = getElementChildValue("distance", el);
                Double distance = distanceStr == null ? null : Double.parseDouble(distanceStr);

                String dateStr = getElementChildValue("creationDate", el);
                LocalDate creationDate = dateStr == null ? null : LocalDate.parse(
                    dateStr, 
                    java.time.format.DateTimeFormatter.ISO_DATE
                );

                Location from = parseLocation((Element)el.getElementsByTagName("from").item(0));
                Location to = parseLocation((Element)el.getElementsByTagName("to").item(0));

                Route route = new Route(
                    id, 
                    name, 
                    creationDate, 
                    from, 
                    to, 
                    distance
                );

                collection.add(route);
            } catch (
                InvalidFieldException | 
                NullPointerException |
                NumberFormatException |
                DateTimeParseException  e
            ) {
                System.out.println("Caught exception while parsing route: ");
                System.out.println(e);
                System.out.println("Skipping invalid route...");
            }
        }

        System.out.println("Parsing finished. Total routes retrieved from file: " + collection.size());

        parsed.setCollection(collection);

        return parsed;
    }

    private static Location parseLocation(Element el) throws InvalidFieldException {
        if (el == null) return null;

        return new Location(
            getElementChildValue("name", el), 
            parseCoordinates((Element)el.getElementsByTagName("coordinates").item(0))
        );
    }

    private static Coordinates parseCoordinates(Element el) throws InvalidFieldException {
        if (el == null) return null;

        String x = el.getAttribute("x");
        String y = el.getAttribute("y");
        String z = el.getAttribute("z");

        return new Coordinates(
            x.isEmpty() ? null : Long.parseLong(x), 
            y.isEmpty() ? null : Double.parseDouble(y), 
            z.isEmpty() ? null : Long.parseLong(z)
        );
    }

    private static String getElementChildValue(String childTag, Element el) {
        Element child = (Element)el.getElementsByTagName(childTag).item(0);

        if (child == null) return null;

        return child.getTextContent();
    }
}
