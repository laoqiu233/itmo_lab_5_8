package com.dmtri.client.collectionmanagers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.dmtri.client.collectionmanagers.xmlcollectionutil.XMLCollectionParser;
import com.dmtri.client.collectionmanagers.xmlcollectionutil.XMLCollectionWriter;
import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;

/**
 * Collection manager which uses an XML file for
 * data storage
 */
public class FileCollectionManager implements SaveableCollectionManager {
    private LinkedList<Route> collection;
    private long nextId;
    private String fileName;

    /**
     * Parses the provided file and stores the generated objects in memory.
     * @param fileName The xml file to use for data storage.
     * @throws IncorrectFileStructureException if the XML file has a incorrect DOM structure. E.g. missing attributes or invalid values.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public FileCollectionManager(String fileName) throws IncorrectFileStructureException, IOException, SAXException, ParserConfigurationException, TransformerException {
        XMLCollectionParser.ParsedCollection parsed = XMLCollectionParser.parse(fileName);

        nextId = parsed.getNextId();
        collection = parsed.getCollection();
        this.fileName = fileName;
    }

    public LinkedList<Route> getCollection() {
        return collection;
    }

    public Route getItemById(long id) {
        return collection.stream().filter(x -> x.getId() == id).findFirst().orElseThrow();
    }

    public void add(Route route) throws InvalidFieldException {
        if (collection.stream().anyMatch(x -> x.getId() == route.getId())) {
            throw new InvalidFieldException("Route with ID " + route.getId() + " already exists in collection.");
        }

        if (route.getId() == nextId) {
            nextId++;
        }

        collection.add(route);
    }

    public void update(Route route) {
        if (!collection.removeIf(x -> x.getId() == route.getId())) {
            throw new NoSuchElementException("Can not find element with ID " + route.getId());
        }

        collection.add(route);
    }

    public void remove(long id) {
        int toRemove = IntStream.range(0, collection.size())
                       .filter(i -> collection.get(i).getId() == id)
                       .findFirst()
                       .orElseThrow();

        collection.remove(toRemove);
    }

    public void clear() {
        collection.clear();
    }

    public long getNextId() {
        return nextId;
    }

    public void save() throws FileNotFoundException {
        try {
            XMLCollectionWriter.writeCollection(fileName, collection, nextId);
        } catch (
            ParserConfigurationException
            | TransformerException e
        ) {
            // Just log error
            System.out.println("Failed to write collection to file.");
            System.out.println(e);
        }
    }
}
