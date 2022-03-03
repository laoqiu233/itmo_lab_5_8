package com.dmtri.client.commands;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class ExitCommand extends AbstractCommand {
    private BasicUserIO io;

    public ExitCommand(BasicUserIO io) {
        super("exit");

        this.io = io;
    }

    public String getUsage() {
        return TerminalColors.colorString("exit", TerminalColors.GREEN) + " - exits the program";
    }

    public void execute(String[] args) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        io.writeln("Thank you for using my program :)");

        System.exit(0);
    }
}
