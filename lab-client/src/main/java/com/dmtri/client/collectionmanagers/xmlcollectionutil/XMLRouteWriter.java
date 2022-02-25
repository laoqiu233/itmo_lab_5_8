package com.dmtri.client.collectionmanagers.xmlcollectionutil;

import com.dmtri.common.models.Coordinates;
import com.dmtri.common.models.Location;
import com.dmtri.common.models.Route;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class XMLRouteWriter {
    private XMLRouteWriter() {
    }

    public static Element routeToXML(Document doc, Route route) {
        Element el = doc.createElement("Route");
        el.setAttribute("id", Long.toString(route.getId()));

        Element name = doc.createElement("name");
        name.setTextContent(route.getName());

        Element distance = doc.createElement("distance");
        distance.setTextContent(Double.toString(route.getDistance()));

        Element creationDate = doc.createElement("creationDate");
        creationDate.setTextContent(route.getCreationDate().toString());

        Element from = locationToXML(doc, route.getFrom(), "from");
        Element to = locationToXML(doc, route.getTo(), "to");

        el.appendChild(name);
        el.appendChild(distance);
        el.appendChild(creationDate);
        el.appendChild(from);
        el.appendChild(to);

        return el;
    }

    public static Element locationToXML(Document doc, Location location, String tagName) {
        Element el = doc.createElement(tagName);

        Element coordinates = coordinatesToXML(doc, location.getCoordinates());
        Element name = doc.createElement("name");
        name.setTextContent(location.getName());

        el.appendChild(coordinates);
        el.appendChild(name);

        return el;
    }

    public static Element coordinatesToXML(Document doc, Coordinates coordinates) {
        Element el = doc.createElement("coordinates");
        if (coordinates.getX() != null) {
            el.setAttribute("x", Long.toString(coordinates.getX()));
        }
        el.setAttribute("y", Double.toString(coordinates.getY()));
        if (coordinates.getZ() != null) {
            el.setAttribute("z", Long.toString(coordinates.getZ()));
        }

        return el;
    }
}
