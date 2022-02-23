package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;

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

    public void execute(String[] args) {
        //if (args.length > 0) throw new TooManyArgumentsException("show", 0);

        col.getCollection().stream().forEach(x -> io.writeln(x.toString() + '\n'));
    }
}
