package com.dmtri.client.commands;

public abstract class AbstractCommand {
    final private String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void checkArgs(String[] args);
    public abstract void execute(String[] args);
    public abstract String getUsage();
}
