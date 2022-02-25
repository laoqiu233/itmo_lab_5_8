package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.collectionmanagers.xmlcollectionutil.XMLCollectionParser;
import com.dmtri.client.collectionmanagers.xmlcollectionutil.XMLCollectionWriter;
import com.dmtri.client.collectionmanagers.xmlcollectionutil.XMLRouteWriter;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.models.Route;
import com.dmtri.common.util.TerminalColors;

public class RemoveFirstCommand extends AbstractCommand {
    BasicUserIO io;
    CollectionManager col;

    public RemoveFirstCommand(BasicUserIO io, CollectionManager col) {
        super("remove_first");
        this.io = io;
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("remove_first", TerminalColors.GREEN)
             + " - removes the first element in the collection";
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            throw new IllegalArgumentException("head command takes no arguments, recieved " + args.length);
        }

        if (col.getCollection().isEmpty()) {
            io.writeln("The collection is empty");
            return;
        }

        Route route = col.getCollection().getFirst();
        io.writeln("The following object will be removed:");
        io.writeln(route);

        col.remove(route.getId());
    }
}
