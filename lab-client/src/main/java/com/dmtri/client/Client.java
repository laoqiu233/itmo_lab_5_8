package com.dmtri.client;

import com.dmtri.client.collectionmanagers.FileCollectionManager;
import com.dmtri.client.commandhandlers.BasicCommandHandler;
import com.dmtri.client.commandhandlers.CommandHandler;
import com.dmtri.client.commands.HelpCommand;
import com.dmtri.client.commands.InfoCommand;
import com.dmtri.client.commands.ShowCommand;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.CommandNotFoundException;

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
        
        while (true) {
            io.write("> ");
            String input = io.read();

            try {
                ch.handle(input);
            } catch (CommandNotFoundException e) {
                io.writeln(e.getMessage());
            }
        }
    }
}
