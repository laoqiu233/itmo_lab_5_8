package com.dmtri.client.commands;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.util.TerminalColors;

public class ExitCommand extends AbstractCommand {
    public ExitCommand() {
        super("exit");
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("exit", TerminalColors.GREEN) + " - exits the program";
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) {
        io.writeln("Thanks for using my program :)");
        System.exit(0);

        return null;
    }

    @Override
    public Response execute(Request request) {
        throw new UnsupportedOperationException();
    }
}
