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

    public static Route parseRoute(BasicUserIO io, Long id, String name, Double distance) throws InvalidFieldException {
        Route.validator.validateId(id);
        Route.validator.validateName(name);
        Route.validator.validateDistance(distance);
        
        return new Route(
            id, 
            name, 
            BasicParsers.Repeater.doUntilGet(RouteMaker::parseCreationDate, io), 
            BasicParsers.Repeater.doUntilGet(
                io_ -> {
                    return parseLocation(
                        io_,
                        "Creating start location",
                        Route.validator::validateFrom
                    );
                },
                io
            ), 
            BasicParsers.Repeater.doUntilGet(
                io_ -> {
                    return parseLocation(
                        io_,
                        "Creating end location",
                        Route.validator::validateTo
                    );
                },
                io
            ), 
            distance
        );
    }

    public static LocalDate parseCreationDate(BasicUserIO io) throws InvalidFieldException {
        LocalDate creationDate = BasicParsers.parseLocalDate(
            io,
            "Enter creation date of route: ",
            "Invalid date entered."
        );

        Route.validator.validateCreationDate(creationDate);

        return creationDate;
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
