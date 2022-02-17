package CommandUtils.Commands;

import UserIO.BasicUserIO;
import CollectionManagers.CollectionManager;
import Exceptions.TooManyArgumentsException;

public class ShowCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;

    public ShowCommand(BasicUserIO io, CollectionManager col) {
        super("show", 0);

        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return "Outputs every item in the collection.";
    }

    public void execute(String[] args) throws TooManyArgumentsException {
        if (args.length > 0) throw new TooManyArgumentsException("show", 0);

        col.getCollection().stream().forEach(x -> io.writeln(x));
    }
}