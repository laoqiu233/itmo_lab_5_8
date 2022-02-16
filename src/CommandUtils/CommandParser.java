package CommandUtils;

import java.util.HashMap;

import CommandUtils.Commands.AbstractCommand;

public class CommandParser {
    private HashMap<String, AbstractCommand> commands;
    
    public void CommandParser() {

    }

    public void ParseCommandString(String commandString) {

    }

    public void addCommand(String commandString, AbstractCommand command) {
        commands.put(commandString, command);
    }
}