package com.dmtri.server.collectionmanagers.xmlcollectionutil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Coordinates;
import com.dmtri.common.models.Location;
import com.dmtri.common.models.Route;

import org.w3c.dom.Element;

public final class XMLRouteParser {
    private XMLRouteParser() {
    }

    public static Route parseRoute(Element el) throws InvalidFieldException {
        if (el == null) {
            return null;
        }

        String idStr = el.getAttribute("id");
        Long id = idStr.isEmpty() ? null : Long.parseLong(idStr);
        String name = getElementChildValue("name", el);

        String distanceStr = getElementChildValue("distance", el);
        Double distance;
        try {
            distance = distanceStr == null ? null : Double.parseDouble(distanceStr);
        } catch (NumberFormatException e) {
            throw new InvalidFieldException("Distance is not a valid number", e);
        }

        String dateStr = getElementChildValue("creationDate", el);
        LocalDate creationDate;
        try {
            creationDate = dateStr == null ? null : LocalDate.parse(
                dateStr,
                java.time.format.DateTimeFormatter.ISO_DATE
            );
        } catch (DateTimeParseException e) {
            throw new InvalidFieldException("Creation date is not valid a valid date", e);
        }

        Location from = parseLocation((Element) el.getElementsByTagName("from").item(0));
        Location to = parseLocation((Element) el.getElementsByTagName("to").item(0));

        Route route = new Route(id, name, creationDate, from, to, distance);

        return route;
    }

    private static Location parseLocation(Element el) throws InvalidFieldException {
        if (el == null) {
            return null;
        }

        return new Location(
            getElementChildValue("name", el),
            parseCoordinates((Element) el.getElementsByTagName("coordinates").item(0))
        );
    }

    private static Coordinates parseCoordinates(Element el) throws InvalidFieldException {
        if (el == null) {
            return null;
        }

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
        Element child = (Element) el.getElementsByTagName(childTag).item(0);

        if (child == null) {
            return null;
        }

        return child.getTextContent();
    }
}
