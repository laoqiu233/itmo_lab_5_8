package com.dmtri.common.commands;

import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.userio.BasicUserIO;

public abstract class AbstractCommand {
    private final String name;
    private final boolean requireAuth;

    public AbstractCommand(String name) {
        this.name = name;
        requireAuth = true;
    }

    public AbstractCommand(String name, boolean requireAuth) {
        this.name = name;
        this.requireAuth = requireAuth;
    }

    public String getName() {
        return name;
    }

    public boolean requiresAuth() {
        return requireAuth;
    }

    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length != 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        return new RequestBody(args);
    }

    public abstract Response execute(Request request, Long userId) throws InvalidRequestException;
    public abstract String getUsage();
}
