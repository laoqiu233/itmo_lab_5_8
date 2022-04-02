package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;

public class InfoCommand extends AbstractCommand {
    private CollectionManager col;

    public InfoCommand(CollectionManager col) {
        super("info");

        this.col = col;
    }

    public String getUsage() {
        return "Displays information about the collection, types, item count, etc.";
    }

    @Override
    public Response execute(Request request) throws InvalidRequestException {
        StringBuilder sb = new StringBuilder();
        sb.append("Collection type: " + col.getCollection().getClass().getSimpleName() + '\n');
        sb.append("Item count: " + col.getCollection().size() + '\n');

        return new Response(sb.toString());
    }
}
