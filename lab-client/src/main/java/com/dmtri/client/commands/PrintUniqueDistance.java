package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class PrintUniqueDistance extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public PrintUniqueDistance(BasicUserIO io, CollectionManager col) {
        super("print_unique_distance");
        this.io = io;
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("print_unique_distance", TerminalColors.GREEN)
             + " - prints all distinct distances";
    }

    @Override
    public void execute(String[] args) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        col.getCollection().stream()
        .filter(x -> x.getDistance() != null)
        .map(x -> x.getDistance())
        .distinct()
        .forEach(io::writeln);
    }
}
