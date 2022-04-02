package com.dmtri.client.commands;

import java.util.NoSuchElementException;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.util.TerminalColors;

public class RemoveByIdCommand extends AbstractCommand {
    private CollectionManager col;

    public RemoveByIdCommand(CollectionManager col) {
        super("remove_by_id");
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("remove_by_id [id]", TerminalColors.GREEN)
             + " - removes an item with the specified id from collection.";
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        try {
            Long.parseLong(args[0]);
            return new RequestBody(args);
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("Failed to convert " + args[0] + " to a number", e);
        } catch (NoSuchElementException e) {
            throw new CommandArgumentException("Can not find element with id " + args[0] + " in collection", e);
        }
    }

    @Override
    public Response execute(Request request) throws InvalidRequestException {
        try {
            col.remove(Long.valueOf(request.getBody().getArg(0)));
            return new Response();
        } catch (NumberFormatException e) {
            throw new InvalidRequestException(new CommandArgumentException("Failed to convert " + request.getBody().getArg(0) + " to a number", e));
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException(new CommandArgumentException("Can not find element with id " + request.getBody().getArg(0) + " in collection", e));
        }
    }
}
