package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.util.TerminalColors;

public class ClearCommand extends AbstractCommand {
    private CollectionManager col;

    public ClearCommand(CollectionManager col) {
        super("clear");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("clear", TerminalColors.GREEN) + " - clears the entire collection";
    }

    @Override
    public Response execute(Request request) {
        col.clear();

        return new Response("Collection cleared");
    }
}
