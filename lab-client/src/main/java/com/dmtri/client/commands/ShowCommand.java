package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithRoutes;

public class ShowCommand extends AbstractCommand {
    private CollectionManager col;

    public ShowCommand(CollectionManager col) {
        super("show");
        this.col = col;
    }

    public String getUsage() {
        return "Outputs every item in the collection.";
    }

    @Override
    public Response execute(Request request) {
        Route[] a = new Route[col.getCollection().size()];
        return new ResponseWithRoutes(col.getCollection().toArray(a));
    }
}
