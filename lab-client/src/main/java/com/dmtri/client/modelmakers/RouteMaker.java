package com.dmtri.client.modelmakers;

import java.time.LocalDate;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.AbstractValidator;
import com.dmtri.common.models.Location;
import com.dmtri.common.models.Route;

public final class RouteMaker {
    private RouteMaker() {
    }

    public static Route parseRoute(BasicUserIO io, Long id) throws InvalidFieldException {
        Route.VALIDATOR.validateId(id);

        return new Route(
            id,
            BasicParsers.Repeater.doUntilGet(RouteMaker::parseName, io),
            LocalDate.now(),
            BasicParsers.Repeater.doUntilGet(
                io_ -> {
                    return parseLocation(
                        io_,
                        "Creating start location",
                        Route.VALIDATOR::validateFrom
                    );
                },
                io
            ),
            BasicParsers.Repeater.doUntilGet(
                io_ -> {
                    return parseLocation(
                        io_,
                        "Creating end location",
                        Route.VALIDATOR::validateTo
                    );
                },
                io
            ),
            BasicParsers.Repeater.doUntilGet(RouteMaker::parseDistance, io)
        );
    }

    public static String parseName(BasicUserIO io) throws InvalidFieldException {
        String res = BasicParsers.parseString(io, "Enter route name: ");
        Route.VALIDATOR.validateName(res);
        return res;
    }

    public static Double parseDistance(BasicUserIO io) throws InvalidFieldException {
        Double res = BasicParsers.parseDouble(io, "Enter route distance: ", "Route distance must be greater than 1");
        Route.VALIDATOR.validateDistance(res);
        return res;
    }

    public static Location parseLocation(BasicUserIO io, String prompt, AbstractValidator<Location> validator) throws InvalidFieldException {
        try {
            io.writeln(prompt);
            Location from = LocationMaker.parseLocation(io);
            validator.validate(from);
            return from;
        } catch (InvalidFieldException e) {
            throw new InvalidFieldException("Failed to create location for route", e);
        }
    }
}
