package com.dmtri.client.commands;

import com.dmtri.common.exceptions.CommandArgumentException;

public abstract class AbstractCommand {
    private final String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void execute(String[] args) throws CommandArgumentException;
    public abstract String getUsage();
}
