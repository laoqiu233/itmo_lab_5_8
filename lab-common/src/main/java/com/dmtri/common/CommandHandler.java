package com.dmtri.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.commands.AbstractCommand;
import com.dmtri.common.commands.AddCommand;
import com.dmtri.common.commands.ClearCommand;
import com.dmtri.common.commands.ExecuteScriptCommand;
import com.dmtri.common.commands.ExitCommand;
import com.dmtri.common.commands.HeadCommand;
import com.dmtri.common.commands.HelpCommand;
import com.dmtri.common.commands.InfoCommand;
import com.dmtri.common.commands.PrintUniqueDistance;
import com.dmtri.common.commands.RemoveAllByDistanceCommand;
import com.dmtri.common.commands.RemoveByIdCommand;
import com.dmtri.common.commands.RemoveFirstCommand;
import com.dmtri.common.commands.RemoveGreaterCommand;
import com.dmtri.common.commands.ShowCommand;
import com.dmtri.common.commands.SumOfDistanceCommand;
import com.dmtri.common.commands.UpdateCommand;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.CommandNotFoundException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithException;
import com.dmtri.common.userio.BasicUserIO;

public class CommandHandler {
    private HashMap<String, AbstractCommand> commands = new HashMap<>();

    public static CommandHandler standardCommandHandler(CollectionManager cm) {
        CommandHandler ch = new CommandHandler();
        ch.addCommand(new HelpCommand(ch));
        ch.addCommand(new InfoCommand(cm));
        ch.addCommand(new ShowCommand(cm));
        ch.addCommand(new AddCommand(cm));
        ch.addCommand(new RemoveByIdCommand(cm));
        ch.addCommand(new UpdateCommand(cm));
        ch.addCommand(new ExitCommand());
        ch.addCommand(new ClearCommand(cm));
        ch.addCommand(new SumOfDistanceCommand(cm));
        ch.addCommand(new HeadCommand(cm));
        ch.addCommand(new RemoveFirstCommand(cm));
        ch.addCommand(new RemoveGreaterCommand(cm));
        ch.addCommand(new RemoveAllByDistanceCommand(cm));
        ch.addCommand(new PrintUniqueDistance(cm));
        ch.addCommand(new ExecuteScriptCommand());

        return ch;
    }

    public Request handleString(String commandString, BasicUserIO io) throws CommandNotFoundException, CommandArgumentException {
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

    public Response handleRequest(Request request) {
        if (commands.get(request.getCommandName()) != null) {
            try {
                return commands.get(request.getCommandName()).execute(request);
            } catch (InvalidRequestException e) {
                return new ResponseWithException(e);
            }
        }

        return new ResponseWithException(new CommandNotFoundException(request.getCommandName()));
    }

    public void addCommand(AbstractCommand command) {
        commands.put(command.getName(), command);
    }

    public Map<String, AbstractCommand> getCommands() {
        return this.commands;
    }
}
