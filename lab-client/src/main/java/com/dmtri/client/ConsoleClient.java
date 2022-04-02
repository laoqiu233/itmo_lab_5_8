package com.dmtri.client;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.CommandNotFoundException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithException;
import com.dmtri.common.network.ResponseWithRoutes;
import com.dmtri.common.util.TerminalColors;

public class ConsoleClient {
    private BasicUserIO io;
    private CommandHandler ch;
    private String inputPrefix = "> ";

    public ConsoleClient(BasicUserIO io, CommandHandler ch) {
        this.io = io;
        this.ch = ch;
    }

    public static void writeTrace(Exception e, BasicUserIO io) {
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

    public void run() {
        String input;
        while ((input = io.read(inputPrefix)) != null) {
            try {
                Request request = ch.handle(input, io);

                if (request != null) {
                    Response response = ch.getCommands().get(request.getCommandName()).execute(request);

                    io.writeln(response.getMessage());

                    if (response instanceof ResponseWithException) {
                        ResponseWithException rwe = (ResponseWithException) response;
                        writeTrace(rwe.getException(), io);
                    } else if (response instanceof ResponseWithRoutes) {
                        ResponseWithRoutes rwr = (ResponseWithRoutes) response;

                        for (int i = 0; i < rwr.getRoutesCount(); i++) {
                            io.writeln(rwr.getRoute(i));
                        }
                    }
                }
            } catch (
                CommandNotFoundException
                | CommandArgumentException
                | InvalidRequestException e
            ) {
                writeTrace(e, io);
            }
        }
    }
}
