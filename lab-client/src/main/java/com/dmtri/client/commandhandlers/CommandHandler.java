package com.dmtri.client.commandhandlers;

import java.util.Map;

import com.dmtri.client.commands.AbstractCommand;
import com.dmtri.common.exceptions.CommandNotFoundException;

public interface CommandHandler {
    void handle(String commandString) throws CommandNotFoundException;
    void addCommand(AbstractCommand command);
    Map<? extends String, ? extends AbstractCommand> getCommands();
}
