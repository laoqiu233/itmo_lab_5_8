package CollectionManagers;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import Exceptions.IncorrectFileStructureException;
import Exceptions.ItemNotFoundException;
import Models.Coordinates;
import Models.Location;
import Models.Route;

/**
 * Collection manager which uses an XML file for 
 * data storage
 */
public class FileCollectionManager implements CollectionManager {
    private LinkedList<Route> collection;

    /**
     * Parses the provided file and stores the generated objects in memory.
     * @param fileName The xml file to use for data storage.
     * @throws IncorrectFileStructureException if the XML file has a incorrect DOM structure. E.g. missing attributes or invalid values.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public FileCollectionManager(String fileName) throws IncorrectFileStructureException, IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));

        Element root = doc.getDocumentElement();

        if (root.getTagName() != "routes") throw new IncorrectFileStructureException("Corrupted file: Incorrect root element");

        NodeList nodes = root.getElementsByTagName("Route");

        collection = new LinkedList<Route>();

        for (int i=0; i<nodes.getLength(); i++) {
            Element el = (Element)nodes.item(i);

            long id;

            try {
                id = Long.parseLong(el.getAttribute("id"));
            } catch (NumberFormatException e) {
                throw new IncorrectFileStructureException("Element " + el + " has invalid id attribute");
            }

            String name;
            
            try {
                name = el.getElementsByTagName("name").item(0).getTextContent();
            } catch (NullPointerException e) {
                throw new IncorrectFileStructureException("Element " + el + " does not have a name defined"); 
            }

            double distance;

            try {
                distance = Double.parseDouble(el.getElementsByTagName("distance").item(0).getTextContent());
            } catch (NumberFormatException e) {
                throw new IncorrectFileStructureException("Element " + el + " has invalid distance value"); 
            } catch (NullPointerException e) {
                throw new IncorrectFileStructureException("Element " + el + " does not have a distance defined");
            }

            java.time.LocalDate creationDate;

            try {
                creationDate = java.time.LocalDate.parse(
                    el.getElementsByTagName("creationDate").item(0).getTextContent(), 
                    java.time.format.DateTimeFormatter.ISO_DATE
                );
            } catch (DateTimeParseException e) {
                throw new IncorrectFileStructureException("Element" + el + " has invalid creationDate value.\n" + e);
            } catch (NullPointerException e) {
                throw new IncorrectFileStructureException("Element " + el + "does not have a creationDate defined");
            }

            class LocationParser {
                Location parseLocation(Element location) throws IncorrectFileStructureException {
                    if (location == null) throw new IncorrectFileStructureException("Element " + el + " does not have its endpoints defined.");

                    String name;

                    try {
                        name = location.getElementsByTagName("name").item(0).getTextContent();
                    } catch (NullPointerException e) {
                        throw new IncorrectFileStructureException("Element " + location + " does not have a name defined"); 
                    }

                    Element coordinates = (Element)location.getElementsByTagName("coordinates").item(0);

                    if (coordinates == null) throw new IncorrectFileStructureException("Element " + location + " does not have its coordinates defined");
                    
                    return new Location(name, parseCoordinates(coordinates));
                } 

                Coordinates parseCoordinates(Element coordinates) throws IncorrectFileStructureException {
                    long x;
                    double y;
                    long z;

                    try {
                        x = Long.parseLong(coordinates.getAttribute("x"));
                        y = Double.parseDouble(coordinates.getAttribute("y"));
                        z = Long.parseLong(coordinates.getAttribute("z"));
                    } catch (NumberFormatException e) {
                        throw new IncorrectFileStructureException("Element" + coordinates + " has invalid coordinates.\n" + e);
                    }

                    return new Coordinates(x, y, z);
                }
            }

            LocationParser lp = new LocationParser();
            Location from = lp.parseLocation((Element)el.getElementsByTagName("from").item(0));
            Location to = lp.parseLocation((Element)el.getElementsByTagName("to").item(0));

            collection.add(new Route(
                id, 
                name, 
                creationDate, 
                from, 
                to, 
                distance
            ));
        }
    }

    public LinkedList<Route> getCollection() {
        return collection;
    }

    public void add(Route route) {
        collection.add(route);
    }

    public void update(Route route) throws ItemNotFoundException {

    }

    public void remove(long id) throws ItemNotFoundException {
        for (int i=0; i<collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                collection.remove(i);
                return;
            }
        }

        throw new ItemNotFoundException("The item with the specified id was not found.");
    }

    public void clear() {
        collection.clear();
    }
}
