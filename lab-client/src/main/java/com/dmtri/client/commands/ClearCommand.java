package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class ClearCommand extends AbstractCommand {
    private CollectionManager col;

    public ClearCommand(CollectionManager col) {
        super("clear");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("clear", TerminalColors.GREEN) + " - clears the entire collection";
    }

    @Override
    public void execute(String[] args) throws CommandArgumentException {
        if (args.length != 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        col.clear();
    }
}
