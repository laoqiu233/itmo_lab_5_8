package com.dmtri.common.commands;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.modelmakers.RouteMaker;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.RequestBodyWithRoute;
import com.dmtri.common.network.Response;
import com.dmtri.common.userio.BasicUserIO;
import com.dmtri.common.util.TerminalColors;

public class UpdateCommand extends AbstractCommand {
    private CollectionManager col;

    public UpdateCommand(CollectionManager col) {
        super("update");
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("update [id]", TerminalColors.GREEN)
             + " - updates the element with the specified id.";
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException(getName(), 1, args.length);
        }

        try {
            Long id = Long.parseLong(args[0]);
            Route route = RouteMaker.parseRoute(io, id);
            return new RequestBodyWithRoute(args, route);
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("id is not a valid number", e);
        } catch (InvalidFieldException e) {
            io.writeln(TerminalColors.colorString("Failed to update route", TerminalColors.RED));
            io.writeln(e);
            return null;
        }
    }

    @Override
    public Response execute(Request request) throws InvalidRequestException {
        if (request.getBody() == null || !(request.getBody() instanceof RequestBodyWithRoute)) {
            throw new InvalidRequestException("Request should have a route attached");
        }

        if (!col.update(((RequestBodyWithRoute) request.getBody()).getRoute())) {
            throw new InvalidRequestException(new CommandArgumentException("No item with specified id was found in collection"));
        }
        return new Response("Updated");
    }
}
