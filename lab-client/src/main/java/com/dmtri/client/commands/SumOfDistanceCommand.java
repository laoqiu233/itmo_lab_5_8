package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class SumOfDistanceCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public SumOfDistanceCommand(BasicUserIO io, CollectionManager col) {
        super("sum_of_distance");
        this.io = io;
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("sum_of_distance", TerminalColors.GREEN)
             + " - sums the distances for all routes and outputs the result.";
    }

    @Override
    public void execute(String[] args) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        Double res = col.getCollection().stream()
                                        .map(r -> r.getDistance())
                                        .reduce((a, b) -> a + b)
                                        .orElse(0d);

        io.writeln("Sum of distances: " + res);
    }
}
