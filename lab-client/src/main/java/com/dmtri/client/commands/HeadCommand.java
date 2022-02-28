package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.util.TerminalColors;

public class HeadCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public HeadCommand(BasicUserIO io, CollectionManager col) {
        super("head");
        this.io = io;
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("head", TerminalColors.GREEN)
             + " - outputs the first element in the collection";
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

        io.writeln(col.getCollection().get(0));
    }
}
