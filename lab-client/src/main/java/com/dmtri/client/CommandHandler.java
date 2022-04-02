package com.dmtri.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.dmtri.client.commands.AbstractCommand;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.CommandNotFoundException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;

public class CommandHandler {
    private HashMap<String, AbstractCommand> commands = new HashMap<>();

    public Request handle(String commandString, BasicUserIO io) throws CommandNotFoundException, CommandArgumentException {
        String[] commandArgs = commandString.trim().split("\\s+");

        AbstractCommand command = commands.get(commandArgs[0]);

        if (command == null) {
            throw new CommandNotFoundException(commandArgs[0]);
        }

        RequestBody body = command.packageBody(Arrays.copyOfRange(commandArgs, 1, commandArgs.length), io);
        if (body == null) {
            return null;
        }
        Request request = new Request(command.getName(), body);

        return request;
    }

    public void addCommand(AbstractCommand command) {
        commands.put(command.getName(), command);
    }

    public Map<String, AbstractCommand> getCommands() {
        return this.commands;
    }
}
