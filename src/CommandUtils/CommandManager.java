package CommandUtils;

import java.util.Arrays;
import java.util.HashMap;

import CommandUtils.Commands.AbstractCommand;
import Exceptions.CommandNotFoundException;
import Exceptions.TooManyArgumentsException;
import UserIO.BasicUserIO;

public class CommandManager {
    private HashMap<String, AbstractCommand> commands = new HashMap<>();
    private BasicUserIO io;
    
    public CommandManager(BasicUserIO io) {
        this.io = io;

        addCommand(new AbstractCommand("help", 1) {
            public String getUsage() {
                return "Usage: help [command]. Displays usage of every command or optionally a specific command.";
            }

            public void execute(String[] args) throws TooManyArgumentsException {
                checkArgs(args);

                if (args.length == 1) {
                    AbstractCommand command = commands.get(args[0]);
                    CommandManager.this.io.writeln("- " + args[0]);
                    CommandManager.this.io.writeln(command.getUsage());
                    return;
                }

                commands.entrySet().forEach(x -> {
                    CommandManager.this.io.writeln("- " + x.getKey());
                    CommandManager.this.io.writeln(x.getValue().getUsage());
                });
            }
        });
    }

    public void setIO(BasicUserIO io) {
        this.io = io;
    }

    public void handle(String commandString) throws CommandNotFoundException, TooManyArgumentsException {
        String[] commandArgs = commandString.trim().split("\s+");

        AbstractCommand command = commands.get(commandArgs[0]);

        if (command == null) throw new CommandNotFoundException(commandArgs[0]);

        command.execute(Arrays.copyOfRange(commandArgs, 1, commandArgs.length));
    }

    public void addCommand(AbstractCommand command) {
        commands.put(command.getName(), command);
    }
}