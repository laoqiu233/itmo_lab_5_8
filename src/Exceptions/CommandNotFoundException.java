package Exceptions;

public class CommandNotFoundException extends Exception {
    public CommandNotFoundException(String name) {
        super("Cannot find command with the name " + name + ".");
    }
}