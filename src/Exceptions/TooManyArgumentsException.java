package Exceptions;

public class TooManyArgumentsException extends Exception {
    public TooManyArgumentsException(String command, int n) {
        super("Too many arguments passed to " + command + ", expected " + n + ".");
    }
}
