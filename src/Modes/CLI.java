package Modes;

import java.io.IOException;

import CollectionManagers.FileCollectionManager;
import CommandUtils.CommandManager;
import CommandUtils.Commands.InfoCommand;
import CommandUtils.Commands.ShowCommand;
import Exceptions.CommandNotFoundException;
import Exceptions.TooManyArgumentsException;
import UserIO.BasicUserIO;
public class CLI implements Mode {
    public void run() {
        String fileName = System.getenv("FILENAME");
        FileCollectionManager col;

        try {
            col = new FileCollectionManager(fileName); 
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        
        BasicUserIO io = new BasicUserIO();
        CommandManager handler = new CommandManager(io);
        handler.addCommand(new InfoCommand(io, col));
        handler.addCommand(new ShowCommand(io, col));

        while (true) {
            try {
                io.write("> ");
                String input = io.read();
                handler.handle(input);
            } catch (TooManyArgumentsException e) {
                System.out.println(e.getMessage());
            } catch (CommandNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Something went wrong: " + e);
            }
        }
    }
}
