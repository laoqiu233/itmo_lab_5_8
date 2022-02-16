package CommandUtils.Commands;

public interface AbstractCommand {
    void execute(String[] args);
    String getUsage();
    int argumentCount();
}
