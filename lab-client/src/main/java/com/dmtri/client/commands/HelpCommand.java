package com.dmtri.client.commands;

import com.dmtri.client.commandhandlers.CommandHandler;
import com.dmtri.client.userio.BasicUserIO;

public class HelpCommand extends AbstractCommand {
    private BasicUserIO io;
    private CommandHandler ch;

    public HelpCommand(BasicUserIO io, CommandHandler ch) {
        super("help");

        this.io = io;
        this.ch = ch;
    }

    public void checkArgs(String args[]) {

    }

    public String getUsage() {
        return "help - displays usage of all available commands\n" + 
               "help [command] - displays the usage of a single command";
    }

    public void execute(String[] args) {
        checkArgs(args);
        
        if (args.length == 1) {
            AbstractCommand command = ch.getCommands().get(args[0]);
            io.writeln("- " + command.getName());
            io.writeln(command.getUsage());
            return;
        }

        ch.getCommands().values().forEach(c -> {
            io.writeln("- " + c.getName());
            io.writeln(c.getUsage());
            io.writeln("");
        });
    }
}
