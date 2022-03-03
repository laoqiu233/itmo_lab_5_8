package com.dmtri.client.collectionmanagers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
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
    private List<Route> collection;
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

    public List<Route> getCollection() {
        // TODO: Make copy instead of the actual list
        return collection;
    }

    public Route getItemById(long id) {
        return collection.stream()
                         .filter(x -> x.getId() == id)
                         .findFirst()
                         .orElseThrow(() -> new NoSuchElementException("Cannot find item with id " + id));
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
            throw new NoSuchElementException("Can not find item with id " + route.getId());
        }

        collection.add(route);
    }

    public void remove(long id) {
        boolean res = collection.removeIf(x -> x.getId() == id);

        if (!res) {
            throw new NoSuchElementException("Cannot find item with id " + id);
        }
    }

    public int removeIf(Predicate<? super Route> predicate) {
        // Using streams instead of collection.removeIf
        // Because I want to return number of elements removed
        List<Long> toRemove = collection.stream()
            .filter(predicate)
            .map(x -> x.getId())
            .collect(Collectors.toList());

        toRemove.forEach(this::remove);

        return toRemove.size();
    }

    public void clear() {
        collection.clear();
        nextId = 1;
    }

    public long getNextId() {
        return nextId;
    }

    public void save() throws FileNotFoundException {
        try {
            XMLCollectionWriter.writeCollection(fileName, collection, nextId);
        } catch (
            ParserConfigurationException
            | IOException
            | TransformerException e
        ) {
            // Just log error
            System.out.println("Failed to write collection to file.");
            System.out.println(e);
        }
    }
}
