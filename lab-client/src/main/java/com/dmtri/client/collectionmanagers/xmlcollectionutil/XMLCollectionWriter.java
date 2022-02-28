package com.dmtri.client.collectionmanagers.xmlcollectionutil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.dmtri.common.models.Route;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class XMLCollectionWriter {
    private XMLCollectionWriter() {
    }

    private static Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();

        return doc;
    }

    private static Transformer createTransformer() throws TransformerException {
        TransformerFactory tff = TransformerFactory.newInstance();
        Transformer tf = tff.newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return tf;
    }

    public static void writeElementToConsole(Element el) throws TransformerException {
        Transformer tf = createTransformer();
        DOMSource source = new DOMSource(el);

        tf.transform(source, new StreamResult(System.out));
    }

    public static void writeCollection(String fileName, List<Route> collection, long nextId) throws ParserConfigurationException, TransformerException, FileNotFoundException {
        Document doc = createDocument();

        Element root = doc.createElement("routes");
        root.setAttribute("nextId", Long.toString(nextId));
        doc.appendChild(root);

        collection.stream().forEach(x -> root.appendChild(XMLRouteWriter.routeToXML(doc, x)));

        Transformer tf = createTransformer();
        DOMSource source = new DOMSource(doc);

        // Запись данных в файл необходимо реализовать с помощью класса java.io.BufferedOutputStream
        BufferedOutputStream buffered = new BufferedOutputStream(new FileOutputStream(new File(fileName)));

        tf.transform(source, new StreamResult(buffered));
    }
}
