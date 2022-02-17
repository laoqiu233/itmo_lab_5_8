package CommandUtils.Commands;

import UserIO.BasicUserIO;
import CollectionManagers.CollectionManager;
import Exceptions.TooManyArgumentsException;

public class InfoCommand extends AbstractCommand {
    private BasicUserIO io;
    private CollectionManager col;
    
    public InfoCommand(BasicUserIO io, CollectionManager col) {
        super("info", 0);

        this.io = io;
        this.col = col;
    }

    public String getUsage() {
        return "Displays information about the collection, types, item count, etc.";
    }

    public void execute(String[] args) throws TooManyArgumentsException {
        if (args.length > 0) throw new TooManyArgumentsException("info", 0);

        io.writeln("Item count: " + col.getCollection().size());
    }
}
