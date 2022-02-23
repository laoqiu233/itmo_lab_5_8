package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;

public class InfoCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public InfoCommand(BasicUserIO io, CollectionManager col) {
        super("info");

        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return "Displays information about the collection, types, item count, etc.";
    }

    public void checkArgs(String[] args) {

    }

    public void execute(String[] args) {
        //if (args.length > 0) throw new TooManyArgumentsException("info", 0);

        io.writeln("Item count: " + col.getCollection().size());
    }
}
