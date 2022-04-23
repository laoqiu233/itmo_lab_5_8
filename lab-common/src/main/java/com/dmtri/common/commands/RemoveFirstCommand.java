package com.dmtri.common.commands;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.exceptions.UnauthorizedException;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithException;
import com.dmtri.common.network.ResponseWithRoutes;
import com.dmtri.common.util.TerminalColors;

public class RemoveFirstCommand extends AbstractCommand {
    private CollectionManager col;

    public RemoveFirstCommand(CollectionManager col) {
        super("remove_first");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("remove_first", TerminalColors.GREEN)
             + " - removes the first element in the collection";
    }

    @Override
    public Response execute(Request request, Long userId) throws InvalidRequestException {
        if (col.getCollection().isEmpty()) {
            return new Response("The collection is empty");
        }

        Route route = col.getCollection().get(0);

        if (route.getOwnerId() != userId) {
            return new ResponseWithException(new UnauthorizedException());
        }

        col.remove(route.getId());
        return new ResponseWithRoutes("The following route is removed: ", new Route[] {route});
    }
}
