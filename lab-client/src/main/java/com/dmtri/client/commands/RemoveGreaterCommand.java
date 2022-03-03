package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.modelmakers.RouteMaker;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;
import com.dmtri.common.util.TerminalColors;

public class RemoveGreaterCommand extends AbstractCommand {
    private static final int ARGUMENTS_LENGTH = 3;
    private BasicUserIO io;
    private CollectionManager col;

    public RemoveGreaterCommand(BasicUserIO io, CollectionManager col) {
        super("remove_greater");
        this.io = io;
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("remove_greater [id] [name] [distance]", TerminalColors.GREEN)
             + " - creates a temporary new item, then removes all items in the collection greater than the new item.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length != ARGUMENTS_LENGTH) {
            throw new IllegalArgumentException("remove_greater takes exactly " + ARGUMENTS_LENGTH + " arguments, recieved " + args.length);
        }

        try {
            Long id = Long.parseLong(args[0]);
            String name = args[1];
            Double distance = Double.parseDouble(args[2]);

            Route temp = RouteMaker.parseRoute(io, id, name, distance);

            int res = col.removeIf(x -> x.compareTo(temp) > 0);

            io.writeln("Removed " + res + " items.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid arguments entered", e);
        } catch (InvalidFieldException e) {
            io.writeln("Failed to create the temporary item.");
            io.writeln(e);
        }
    }
}
