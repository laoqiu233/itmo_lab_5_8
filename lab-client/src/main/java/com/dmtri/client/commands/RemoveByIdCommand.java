package com.dmtri.client.commands;

import java.util.NoSuchElementException;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.common.util.TerminalColors;

public class RemoveByIdCommand extends AbstractCommand {
    private CollectionManager col;

    public RemoveByIdCommand(CollectionManager col) {
        super("remove_by_id");
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("remove_by_id [id]", TerminalColors.GREEN)
             + " - removes an item with the specified id from collection.";
    }

    public void execute(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("remove_by_id takes exactly 1 positional argument, recieved " + args.length);
        }

        try {
            Long id = Long.parseLong(args[0]);
            col.remove(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to convert " + args[0] + " to a number", e);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Can not find element with id " + args[0] + " in collection", e);
        }
    }
}
