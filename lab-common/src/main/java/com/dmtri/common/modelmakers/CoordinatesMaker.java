package com.dmtri.common.modelmakers;

import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Coordinates;
import com.dmtri.common.userio.BasicUserIO;

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
        Coordinates.VALIDATOR.validateX(x);
        return x;
    }

    public static Double parseY(BasicUserIO io) throws InvalidFieldException {
        Double y = BasicParsers.parseDouble(
            io,
            "Enter Y coordinate: ",
            "Invalid Y coordinate entered"
        );
        Coordinates.VALIDATOR.validateY(y);
        return y;
    }

    public static Long parseZ(BasicUserIO io) throws InvalidFieldException {
        Long z = BasicParsers.parseLong(
            io,
            "Enter Z coordinate (Press enter to leave null): ",
            "Invalid Z coordinate entered"
        );
        Coordinates.VALIDATOR.validateZ(z);
        return z;
    }
}
