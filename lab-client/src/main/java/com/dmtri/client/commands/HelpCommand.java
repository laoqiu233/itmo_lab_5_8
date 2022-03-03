package com.dmtri.client.commands;

import com.dmtri.client.commandhandlers.CommandHandler;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.util.TerminalColors;

public class HelpCommand extends AbstractCommand {
    private BasicUserIO io;
    private CommandHandler ch;

    public HelpCommand(BasicUserIO io, CommandHandler ch) {
        super("help");

        this.io = io;
        this.ch = ch;
    }

    public String getUsage() {
        return TerminalColors.colorString("help", TerminalColors.GREEN)
             + " - displays usage of all available commands\n"
             + TerminalColors.colorString("help [command]", TerminalColors.GREEN)
             + " - displays the usage of a single command";
    }

    public void execute(String[] args) throws CommandArgumentException {
        if (args.length == 1) {
            AbstractCommand command = ch.getCommands().get(args[0]);
            if (command == null) {
                io.writeln(TerminalColors.colorString("No command with name " + args[0] + " was found.", TerminalColors.RED));
            }
            io.writeln("- " + TerminalColors.colorString(command.getName(), TerminalColors.GREEN));
            io.writeln(command.getUsage());
            return;
        } else if (args.length > 1) {
            throw new CommandArgumentException("help command takes 1 or no arguments. Recieved " + args.length);
        }

        ch.getCommands().values().forEach(c -> {
            io.writeln("- " + TerminalColors.colorString(c.getName(), TerminalColors.GREEN));
            io.writeln(c.getUsage());
            io.writeln("");
        });
    }
}
