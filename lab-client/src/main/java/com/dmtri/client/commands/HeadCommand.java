package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithRoutes;
import com.dmtri.common.util.TerminalColors;

public class HeadCommand extends AbstractCommand {
    private CollectionManager col;

    public HeadCommand(CollectionManager col) {
        super("head");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("head", TerminalColors.GREEN)
             + " - outputs the first element in the collection";
    }

    @Override
    public Response execute(Request request) throws InvalidRequestException {
        if (col.getCollection().isEmpty()) {
            return new Response("The collection is empty");
        }

        return new ResponseWithRoutes(new Route[] {col.getCollection().get(0)});
    }
}
