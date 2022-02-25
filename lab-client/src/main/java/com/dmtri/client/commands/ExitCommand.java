package com.dmtri.client.commands;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.util.TerminalColors;

public class ExitCommand extends AbstractCommand {
    BasicUserIO io;
    
    public ExitCommand(BasicUserIO io) {
        super("exit");

        this.io = io;
    }

    public String getUsage() {
        return TerminalColors.colorString("exit", TerminalColors.GREEN) + " - exits the program";
    }

    public void execute(String[] args) {
        if (args.length > 0) {
            throw new IllegalArgumentException("exit command takes not arguments, recieved " + args.length);
        }

        io.writeln("Thank you for using my program :)");

        System.exit(0);
    }
}
