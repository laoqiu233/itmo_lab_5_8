package com.dmtri.client.modelmakers;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Coordinates;

public final class CoordinatesMaker {
    private CoordinatesMaker() {
    }

    public static Coordinates parseCoordinates(BasicUserIO io) throws InvalidFieldException {
        return new Coordinates(
            BasicParsers.Repeater.doUntilGet(CoordinatesMaker::parseX, io),
            BasicParsers.Repeater.doUntilGet(CoordinatesMaker::parseY, io),
            BasicParsers.Repeater.doUntilGet(CoordinatesMaker::parseZ, io)
        );
    }

    public static Long parseX(BasicUserIO io) throws InvalidFieldException {
        Long x = BasicParsers.parseLong(
            io,
            "Enter X coordinate (Press enter to leave null): ", 
            "Invalid X coordinate entered"
        );
        Coordinates.validator.validateX(x);
        return x;
    }

    public static Double parseY(BasicUserIO io) throws InvalidFieldException {
        Double y = BasicParsers.parseDouble(
            io,
            "Enter Y coordinate: ",
            "Invalid Y coordinate entered"
        );
        Coordinates.validator.validateY(y);
        return y;
    }

    public static Long parseZ(BasicUserIO io) throws InvalidFieldException {
        Long z = BasicParsers.parseLong(
            io,
            "Enter Z coordinate (Press enter to leave null): ", 
            "Invalid Z coordinate entered"
        );
        Coordinates.validator.validateZ(z);
        return z;
    }
}
