package com.dmtri.client.commands;

import java.io.FileNotFoundException;

import com.dmtri.client.collectionmanagers.SaveableCollectionManager;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class SaveCommand extends AbstractCommand {
    private SaveableCollectionManager col;
    private BasicUserIO io;

    public SaveCommand(BasicUserIO io, SaveableCollectionManager col) {
        super("save");
        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return TerminalColors.colorString("save", TerminalColors.GREEN) + " - saves the collection to XML file";
    }

    public void execute(String[] args) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        try {
            col.save();
        } catch (FileNotFoundException e) {
            io.writeln("Failed to open file while writing.");
            io.writeln(e);
        }
    }
}
