package com.dmtri.client.collectionmanagers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.stream.IntStream;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.models.Route;

/**
 * Collection manager which uses an XML file for 
 * data storage
 */
public class FileCollectionManager implements CollectionManager {
    private LinkedList<Route> collection;
    private long nextId;

    /**
     * Parses the provided file and stores the generated objects in memory.
     * @param fileName The xml file to use for data storage.
     * @throws IncorrectFileStructureException if the XML file has a incorrect DOM structure. E.g. missing attributes or invalid values.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public FileCollectionManager(String fileName) throws IncorrectFileStructureException, IOException, SAXException, ParserConfigurationException {
        XMLCollectionParser.ParsedCollection parsed = XMLCollectionParser.parse(fileName);

        nextId = parsed.getNextId();
        collection = parsed.getCollection();
    }

    public LinkedList<Route> getCollection() {
        return collection;
    }

    public Route getItemById(long id) {
        return collection.stream().filter(x -> x.getId() == id).findFirst().orElseThrow();
    }

    public void add(Route route) {
        collection.add(route);
    }

    public void update(Route route) {

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
}