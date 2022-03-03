package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;

public class ShowCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public ShowCommand(BasicUserIO io, CollectionManager col) {
        super("show");

        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return "Outputs every item in the collection.";
    }

    public void checkArgs(String[] args) {

    }

    public void execute(String[] args) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        col.getCollection().stream().forEach(x -> io.writeln(x.toString() + '\n'));
    }
}
