package com.dmtri.common.commands;

import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithAuthCredentials;
import com.dmtri.common.userio.BasicUserIO;
import com.dmtri.common.usermanagers.AuthCredentials;
import com.dmtri.common.usermanagers.UserManager;

public class RegisterCommand extends AbstractCommand {
    private final UserManager users;

    public RegisterCommand(UserManager users) {
        super("register", false);
        this.users = users;
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        String login = io.read("Enter your username: ");
        io.write("Enter your password: ");
        String password = String.valueOf(System.console().readPassword());

        return new RequestBody(new String[] {login, password});
    }

    @Override
    public Response execute(Request request, String username) throws InvalidRequestException {
        if (request.getBody().getArgsLength() != 2) {
            throw new InvalidRequestException("This operation requires exactly two arguments: Login and password");
        }

        AuthCredentials newCredentials = new AuthCredentials(
            request.getBody().getArg(0),
            request.getBody().getArg(1)
        );

        Long newUserId = users.register(newCredentials);

        if (newUserId == null) {
            return new Response("This username is already taken");
        }

        return new ResponseWithAuthCredentials(newCredentials, "Successfully registered new user. Your id is " + newUserId);
    }

    @Override
    public String getUsage() {
        return "Starts user registration";
    }
}
