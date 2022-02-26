package com.dmtri.client.commands;

import java.util.NoSuchElementException;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.modelmakers.RouteMaker;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;
import com.dmtri.common.util.TerminalColors;

public class UpdateCommand extends AbstractCommand {
    private static final int ARGUMENTS_LENGTH = 3;
    private BasicUserIO io;
    private CollectionManager col;

    public UpdateCommand(BasicUserIO io, CollectionManager col) {
        super("update");

        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("update [id] [name] [distance]", TerminalColors.GREEN)
             + " - updates the element with the specified id.";
    }

    public void execute(String[] args) {
        if (args.length != ARGUMENTS_LENGTH) {
            throw new IllegalArgumentException("update command takes exactly " + ARGUMENTS_LENGTH + " arguments, recieved " + args.length + ".");
        }

        try {
            Long id = Long.parseLong(args[0]);
            Double distance = Double.parseDouble(args[2]);

            // Test to see if there is an item to update
            col.getItemById(id);

            Route route = RouteMaker.parseRoute(io, id, args[1], distance);
            col.update(route);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid arguments entered", e);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("No item with specified id in collection", e);
        } catch (InvalidFieldException e) {
            io.writeln(TerminalColors.colorString("Failed to update route", TerminalColors.RED));
            io.writeln(e);
        }
    }
}
