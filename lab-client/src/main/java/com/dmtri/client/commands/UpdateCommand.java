package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.client.modelmakers.RouteMaker;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;
import com.dmtri.common.util.TerminalColors;

public class UpdateCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public UpdateCommand(BasicUserIO io, CollectionManager col) {
        super("update");

        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("update [id]", TerminalColors.GREEN)
             + " - updates the element with the specified id.";
    }

    public void execute(String[] args) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException(this.getName(), 1, args.length);
        }

        try {
            Long id = Long.parseLong(args[0]);

            // Test to see if there is an item to update
            col.getItemById(id);

            Route route = RouteMaker.parseRoute(io, id);
            if (col.update(route)) {
                throw new CommandArgumentException("No item with specified id was found in collection");
            }
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("id is not a valid number", e);
        } catch (InvalidFieldException e) {
            io.writeln(TerminalColors.colorString("Failed to update route", TerminalColors.RED));
            io.writeln(e);
        }
    }
}
