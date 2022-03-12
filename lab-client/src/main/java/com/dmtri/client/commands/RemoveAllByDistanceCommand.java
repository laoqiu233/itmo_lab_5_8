package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class RemoveAllByDistanceCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public RemoveAllByDistanceCommand(BasicUserIO io, CollectionManager col) {
        super("remove_all_by_distance");
        this.io = io;
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("remove_all_by_distance [distance]", TerminalColors.GREEN)
             + " - removes all items with the specified distance.";
    }

    @Override
    public void execute(String[] args) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        try {
            Double distance = Double.parseDouble(args[0]);
            int res = col.removeIf(x -> x.getDistance().equals(distance));
            io.writeln("Removed " + res + " items");
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("Invalid distance entered.", e);
        }
    }
}
