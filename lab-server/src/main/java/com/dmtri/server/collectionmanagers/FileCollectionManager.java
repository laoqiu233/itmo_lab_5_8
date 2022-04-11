package com.dmtri.server.collectionmanagers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.models.Route;
import com.dmtri.server.collectionmanagers.xmlcollectionutil.XMLCollectionParser;
import com.dmtri.server.collectionmanagers.xmlcollectionutil.XMLCollectionWriter;

/**
 * Collection manager which uses an XML file for
 * data storage
 */
public class FileCollectionManager implements SaveableCollectionManager {
    private static final Logger LOGGER = LogManager.getLogger(FileCollectionManager.class);
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
    public FileCollectionManager(String fileName) throws IncorrectFileStructureException, IOException, SAXException, ParserConfigurationException {
        XMLCollectionParser.ParsedCollection parsed = XMLCollectionParser.parse(fileName);

        nextId = parsed.getNextId();
        collection = parsed.getCollection();
        this.fileName = fileName;
    }

    public List<Route> getCollection() {
        // T ODO: Make copy instead of the actual list
        return collection;
    }

    public Route getItemById(long id) {
        return collection.stream()
                         .filter(x -> x.getId() == id)
                         .findFirst()
                         .orElseThrow(() -> new NoSuchElementException("Cannot find item with id " + id));
    }

    public long add(Route route) {
        route.setId(nextId++);
        collection.add(route);
        return route.getId();
    }

    public boolean update(Route route) {
        if (!collection.removeIf(x -> x.getId() == route.getId())) {
            return false;
        }

        collection.add(route);
        return true;
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

    public void save() throws FileNotFoundException {
        try {
            XMLCollectionWriter.writeCollection(fileName, collection, nextId);
        } catch (
            ParserConfigurationException
            | IOException
            | TransformerException e
        ) {
            // Just log error
            LOGGER.error("Failed to write collection to file.", e);
        }
    }

    @Override
    public double sumOfDistances() {
        return collection.stream()
                        .filter(r -> r.getDistance() != null)
                        .map(r -> r.getDistance())
                        .reduce((a, b) -> a + b)
                        .orElse(0d);
    }

    @Override
    public List<Double> getUniqueDistances() {
        return collection.stream()
            .filter(x -> x.getDistance() != null)
            .map(x -> x.getDistance())
            .distinct()
            .collect(Collectors.toList());
    }
}
