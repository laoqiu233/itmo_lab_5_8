package com.dmtri.common.commands;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.exceptions.ClientSideCommandException;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.userio.BasicUserIO;
import com.dmtri.common.util.TerminalColors;

public class HelpCommand extends AbstractCommand {
    private CommandHandler ch;

    public HelpCommand(CommandHandler ch) {
        super("help");
        this.ch = ch;
    }

    public String getUsage() {
        return TerminalColors.colorString("help", TerminalColors.GREEN)
             + " - displays usage of all available commands\n"
             + TerminalColors.colorString("help [command]", TerminalColors.GREEN)
             + " - displays the usage of a single command";
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length > 1) {
            throw new CommandArgumentException("help command takes 1 or no arguments. Recieved " + args.length);
        } else if (args.length == 1) {
            AbstractCommand command = ch.getCommands().get(args[0]);
            if (command == null) {
                io.writeln(TerminalColors.colorString("No command with name " + args[0] + " was found.", TerminalColors.RED));
            }
            io.writeln("- " + TerminalColors.colorString(command.getName(), TerminalColors.GREEN));
            io.writeln(command.getUsage());
        } else {
            ch.getCommands().values().forEach(c -> {
                io.writeln("- " + TerminalColors.colorString(c.getName(), TerminalColors.GREEN));
                io.writeln(c.getUsage());
                io.writeln("");
            });
        }

        return null;
    }

    @Override
    public Response execute(Request request, String username) throws InvalidRequestException {
        throw new ClientSideCommandException();
    }
}
