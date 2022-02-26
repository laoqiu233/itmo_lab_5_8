package com.dmtri.client.commands;

import java.util.LinkedList;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
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
    public void execute(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("remove_all_by_distance takes exactly 1 argument, recieved " + args.length);
        }

        try {
            Double distance = Double.parseDouble(args[0]);

            LinkedList<Long> toRemove = new LinkedList<>();

            col.getCollection().stream()
                            .filter(x -> x.getDistance().equals(distance))
                            .forEach(x -> toRemove.add(x.getId()));

            toRemove.forEach(col::remove);
            io.writeln("Removed " + toRemove.size() + " items");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid distance entered.", e);
        }
    }
}
