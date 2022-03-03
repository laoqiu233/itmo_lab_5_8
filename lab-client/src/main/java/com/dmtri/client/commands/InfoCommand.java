package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;

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

    public void execute(String[] args) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        io.writeln("Item count: " + col.getCollection().size());
        io.writeln("Next ID to be assigned: " + col.getNextId());
    }
}
