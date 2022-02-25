package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.modelmakers.RouteMaker;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;
import com.dmtri.common.util.TerminalColors;

public class AddCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public AddCommand(BasicUserIO io, CollectionManager col) {
        super("add");
        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("add [name] [distance]", TerminalColors.GREEN)
             + " - Starts route creation";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Add has exactly 2 positional arguments, recieved " + args.length);
        }

        Double distance;

        try {
            distance = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to convert " + args[1] + " to a number", e);
        }

        try {
            Route newRoute = RouteMaker.parseRoute(
                io, 
                col.getNextId(), 
                args[0], 
                distance
            );
            col.add(newRoute);
        } catch (InvalidFieldException e) {
            io.writeln(TerminalColors.colorString("Failed to create new route", TerminalColors.RED));
            io.writeln(e.getMessage());
        }
    }
}
