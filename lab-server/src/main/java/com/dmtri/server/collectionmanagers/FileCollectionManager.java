package com.dmtri.server.collectionmanagers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCollectionManager.class);
    private final Lock lock = new ReentrantLock();
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
        try {
            lock.lock();
            return collection.stream()
                    .filter(x -> x.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Cannot find item with id " + id));
        } finally {
            lock.unlock();
        }
    }

    public long add(Route route) {
        try {
            lock.lock();
            route.setId(nextId++);
            collection.add(route);
            return route.getId();
        } finally {
            lock.unlock();
        }
    }

    public boolean update(Route route) {
        try {
            lock.lock();
            if (!collection.removeIf(x -> x.getId() == route.getId())) {
                return false;
            }

            collection.add(route);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void remove(long id) {
        try {
            lock.lock();
            boolean res = collection.removeIf(x -> x.getId() == id);

            if (!res) {
                throw new NoSuchElementException("Cannot find item with id " + id);
            }
        } finally {
            lock.unlock();
        }
    }

    public int removeIf(Predicate<? super Route> predicate) {
        try {
            lock.lock();
            // Using streams instead of collection.removeIf
            // Because I want to return number of elements removed
            List<Long> toRemove = collection.stream()
                .filter(predicate)
                .map(x -> x.getId())
                .collect(Collectors.toList());

            toRemove.forEach(this::remove);

            return toRemove.size();
        } finally {
            lock.unlock();
        }
    }

    public void save() throws FileNotFoundException {
        try {
            lock.lock();
            XMLCollectionWriter.writeCollection(fileName, collection, nextId);
        } catch (
            ParserConfigurationException
            | IOException
            | TransformerException e
        ) {
            // Just log error
            LOGGER.error("Failed to write collection to file.", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double sumOfDistances() {
        try {
            lock.lock();
            return collection.stream()
                        .filter(r -> r.getDistance() != null)
                        .map(r -> r.getDistance())
                        .reduce((a, b) -> a + b)
                        .orElse(0d);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Double> getUniqueDistances() {
        try {
            lock.lock();
            return collection.stream()
                .filter(x -> x.getDistance() != null)
                .map(x -> x.getDistance())
                .distinct()
                .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }
}
