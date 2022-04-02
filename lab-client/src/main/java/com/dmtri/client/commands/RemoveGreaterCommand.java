package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.modelmakers.RouteMaker;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.RequestBodyWithRoute;
import com.dmtri.common.network.Response;
import com.dmtri.common.util.TerminalColors;

public class RemoveGreaterCommand extends AbstractCommand {
    private CollectionManager col;

    public RemoveGreaterCommand(CollectionManager col) {
        super("remove_greater");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("remove_greater [id]", TerminalColors.GREEN)
             + " - creates a temporary new item, then removes all items in the collection greater than the new item.";
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        try {
            Long id = Long.parseLong(args[0]);
            Route temp = RouteMaker.parseRoute(io, id);
            return new RequestBodyWithRoute(args, temp);
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("Invalid arguments entered", e);
        } catch (InvalidFieldException e) {
            io.writeln("Failed to create the temporary item.");
            io.writeln(e);
        }

        return null;
    }

    @Override
    public Response execute(Request request) throws InvalidRequestException {
        if (request.getBody() == null || !(request.getBody() instanceof RequestBodyWithRoute)) {
            throw new InvalidRequestException("Request should have a route attached");
        }

        Route temp = ((RequestBodyWithRoute) request.getBody()).getRoute();

        int res = col.removeIf(x -> x.compareTo(temp) > 0);

        return new Response("Removed " + res + " items");
    }
}
