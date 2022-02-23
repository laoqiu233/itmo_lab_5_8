package com.dmtri.client.commandhandlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.dmtri.client.commands.AbstractCommand;

import com.dmtri.common.exceptions.CommandNotFoundException;

public class BasicCommandHandler implements CommandHandler {
    private HashMap<String, AbstractCommand> commands = new HashMap<>();

    public void handle(String commandString) throws CommandNotFoundException {
        String[] commandArgs = commandString.trim().split("\\s+");

        AbstractCommand command = commands.get(commandArgs[0]);

        if (command == null) {
            throw new CommandNotFoundException(commandArgs[0]);
        }

        command.execute(Arrays.copyOfRange(commandArgs, 1, commandArgs.length));
    }

    public void addCommand(AbstractCommand command) {
        commands.put(command.getName(), command);
    }

    public Map<String, AbstractCommand> getCommands() {
        return this.commands;
    }
}
