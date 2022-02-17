package CommandUtils.Commands;

import Exceptions.TooManyArgumentsException;

public abstract class AbstractCommand {
    final String name;
    final int argumentCount;

    public AbstractCommand(String name, int argumentCount) {
        this.name = name;
        this.argumentCount = argumentCount;
    }

    public String getName() {
        return name;
    }

    protected void checkArgs(String[] args) throws TooManyArgumentsException {
        if (args.length > argumentCount) throw new TooManyArgumentsException(name, argumentCount);
    }

    public abstract void execute(String[] args) throws TooManyArgumentsException;
    public abstract String getUsage();
}
