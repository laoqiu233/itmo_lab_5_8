package com.dmtri.common.commands;

import com.dmtri.common.collectionmanagers.CollectionManager;
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
    public Response execute(Request request, String username) {
        col.clear();

        return new Response("Collection cleared");
    }
}
