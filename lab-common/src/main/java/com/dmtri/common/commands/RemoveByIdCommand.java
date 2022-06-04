package com.dmtri.common.commands;

import java.util.NoSuchElementException;

import com.dmtri.common.LocaleKeys;
import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.exceptions.UnauthorizedException;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithException;
import com.dmtri.common.userio.BasicUserIO;
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
            Route toRemove = col.getItemById(Long.valueOf(request.getBody().getArg(0)));
            if (toRemove == null) {
                throw new NoSuchElementException();
            }

            if (!toRemove.getOwner().equals(request.getAuth().getLogin())) {
                return new ResponseWithException(new UnauthorizedException());
            }

            col.remove(toRemove.getId());
            return new Response();
        } catch (NumberFormatException e) {
            throw new InvalidRequestException(new CommandArgumentException("Failed to convert " + request.getBody().getArg(0) + " to a number", e), LocaleKeys.INVALID_VALUE);
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException(new CommandArgumentException("Can not find element with id " + request.getBody().getArg(0) + " in collection", e), null);
        }
    }
}
