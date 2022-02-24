package com.dmtri.client.modelmakers;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Coordinates;
import com.dmtri.common.models.Location;

public final class LocationMaker {
    private LocationMaker() {
    }

    public static Location parseLocation(BasicUserIO io) throws InvalidFieldException {
        return new Location(
            BasicParsers.Repeater.doUntilGet(LocationMaker::parseName, io),
            BasicParsers.Repeater.doUntilGet(LocationMaker::parseCoordinates, io)
        );
    }

    public static String parseName(BasicUserIO io) throws InvalidFieldException {
        String name = BasicParsers.parseString(io, "Enter location name: ");
        Location.validator.validateName(name);
        return name;
    }

    public static Coordinates parseCoordinates(BasicUserIO io) throws InvalidFieldException {
        return CoordinatesMaker.parseCoordinates(io);
    }
}
