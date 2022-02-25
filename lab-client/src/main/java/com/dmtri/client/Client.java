package com.dmtri.client;

import com.dmtri.client.collectionmanagers.FileCollectionManager;
import com.dmtri.client.commandhandlers.BasicCommandHandler;
import com.dmtri.client.commandhandlers.CommandHandler;
import com.dmtri.client.commands.AddCommand;
import com.dmtri.client.commands.ExitCommand;
import com.dmtri.client.commands.HelpCommand;
import com.dmtri.client.commands.InfoCommand;
import com.dmtri.client.commands.RemoveByIdCommand;
import com.dmtri.client.commands.SaveCommand;
import com.dmtri.client.commands.ShowCommand;
import com.dmtri.client.commands.UpdateCommand;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandNotFoundException;
import com.dmtri.common.util.TerminalColors;

public final class Client {
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws Exception {
        // Get file name from environment variable
        String fileName = System.getenv("FILENAME");
        FileCollectionManager cm = new FileCollectionManager(fileName);
        CommandHandler ch = new BasicCommandHandler();
        BasicUserIO io = new BasicUserIO();

        ch.addCommand(new HelpCommand(io, ch));
        ch.addCommand(new InfoCommand(io, cm));
        ch.addCommand(new ShowCommand(io, cm));
        ch.addCommand(new AddCommand(io, cm));
        ch.addCommand(new RemoveByIdCommand(cm));
        ch.addCommand(new SaveCommand(io, cm));
        ch.addCommand(new UpdateCommand(io, cm));
        ch.addCommand(new ExitCommand(io));

        while (true) {
            io.write("> ");
            String input = io.read();

            try {
                ch.handle(input);
            } catch (
                CommandNotFoundException
                | IllegalArgumentException e
            ) {
                io.writeln(TerminalColors.colorString(e.toString(), TerminalColors.RED));

                Throwable t = e.getCause();

                while (t != null) {
                    io.writeln(TerminalColors.colorString(t.toString(), TerminalColors.RED));
                    t = t.getCause();
                }

                io.writeln( "Use " 
                          + TerminalColors.colorString("help [command name]", TerminalColors.GREEN) 
                          + " to get more information on usage of commands"
                );
            }
        }
    }
}
