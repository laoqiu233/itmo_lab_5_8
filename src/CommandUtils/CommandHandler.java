package CommandUtils;

import java.util.HashMap;

import CommandUtils.Commands.AbstractCommand;
import UserIO.BasicUserIO;
import CollectionManagers.CollectionManager;

public class CommandParser {
    private HashMap<String, AbstractCommand> commands;
    
    public void CommandParser(CollectionManager col, BasicUserIO io) {

    }

    public void ParseCommandString(String commandString) {

    }

    public void addCommand(String commandString, AbstractCommand command) {
        commands.put(commandString, command);
    }
}