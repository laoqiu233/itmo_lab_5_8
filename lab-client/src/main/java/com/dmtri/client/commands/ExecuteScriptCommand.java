package com.dmtri.client.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class ExecuteScriptCommand extends AbstractCommand {
    private BasicUserIO io;
    private Set<File> openedFiles = new HashSet<>();

    public ExecuteScriptCommand(BasicUserIO io) {
        super("execute_script");
        this.io = io;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("execute_script [fileName]", TerminalColors.GREEN)
             + " - reads and executes the content in file as user input";
    }

    @Override
    public void execute(String[] args) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException(this.getName(), 1, args.length);
        }

        File file = new File(args[0]);

        if (openedFiles.contains(file)) {
            throw new CommandArgumentException("Potential recursion with file \"" + args[0] + '"');
        }

        openedFiles.add(file);

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file)) {
                @Override
                public void close() throws IOException {
                    super.close();
                    ExecuteScriptCommand.this.openedFiles.remove(file);
                }
            };
            // File will be closed by IO manager
            io.addIn(fileReader);
        } catch (FileNotFoundException e) {
            throw new CommandArgumentException("Cannot locate file with the name " + args[0]);
        }
    }
}
