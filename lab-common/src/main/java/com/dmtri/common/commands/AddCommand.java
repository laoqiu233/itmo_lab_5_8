package com.dmtri.common.commands;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.modelmakers.RouteMaker;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.RequestBodyWithRoute;
import com.dmtri.common.network.Response;
import com.dmtri.common.userio.BasicUserIO;
import com.dmtri.common.util.TerminalColors;

public class AddCommand extends AbstractCommand {
    private CollectionManager col;

    public AddCommand(CollectionManager col) {
        super("add");
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("add", TerminalColors.GREEN)
             + " - Starts route creation";
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length != 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        try {
            return new RequestBodyWithRoute(args, RouteMaker.parseRoute(io, 1L));
        } catch (InvalidFieldException e) {
            io.writeln(TerminalColors.colorString("Failed to create new route", TerminalColors.RED));
            io.writeln(e.getMessage());
            return null;
        }
    }

    @Override
    public Response execute(Request request, String username) throws InvalidRequestException {
        if (request.getBody() == null || !(request.getBody() instanceof RequestBodyWithRoute)) {
            throw new InvalidRequestException("Request should have a route attached");
        }

        RequestBodyWithRoute body = (RequestBodyWithRoute) request.getBody();

        body.getRoute().setOwner(username);

        long newId = col.add(body.getRoute());

        if (newId == 0) {
            return new Response("Failed to add route");
        } else {
            return new Response("Route added, assigned id " + newId);
        }
    }
}
