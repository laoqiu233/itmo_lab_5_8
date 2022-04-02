package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.util.TerminalColors;

public class RemoveAllByDistanceCommand extends AbstractCommand {
    private CollectionManager col;

    public RemoveAllByDistanceCommand(CollectionManager col) {
        super("remove_all_by_distance");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("remove_all_by_distance [distance]", TerminalColors.GREEN)
             + " - removes all items with the specified distance.";
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        try {
            Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("Invalid distance entered.", e);
        }

        return new RequestBody(args);
    }

    @Override
    public Response execute(Request request) throws InvalidRequestException {
        try {
            Double distance = Double.parseDouble(request.getBody().getArg(0));
            int res = col.removeIf(x -> (x.getDistance() != null && x.getDistance().equals(distance)));
            return new Response("Removed " + res + " items");
        } catch (NumberFormatException e) {
            throw new InvalidRequestException(new CommandArgumentException("Invalid distance entered.", e));
        }
    }
}
