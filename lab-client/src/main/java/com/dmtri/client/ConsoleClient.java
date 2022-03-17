package com.dmtri.client;

import com.dmtri.client.commandhandlers.CommandHandler;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.CommandNotFoundException;
import com.dmtri.common.util.TerminalColors;

public class ConsoleClient {
    private BasicUserIO io;
    private CommandHandler ch;
    private String inputPrefix = "> ";

    public ConsoleClient(BasicUserIO io, CommandHandler ch) {
        this.io = io;
        this.ch = ch;
    }

    public void run() {
        String input;
        while ((input = io.read(inputPrefix)) != null) {
            try {
                ch.handle(input);
            } catch (
                CommandNotFoundException
                | CommandArgumentException e
            ) {
                io.writeln(TerminalColors.colorString(e.toString(), TerminalColors.RED));

                Throwable t = e.getCause();

                while (t != null) {
                    io.writeln(TerminalColors.colorString(t.toString(), TerminalColors.RED));
                    t = t.getCause();
                }

                io.writeln("Use "
                        + TerminalColors.colorString("help [command name]", TerminalColors.GREEN)
                        + " to get more information on usage of commands"
                );
            }
        }
    }
}
