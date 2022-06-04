package com.dmtri.common.commands;

import com.dmtri.common.LocaleKeys;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithAuthCredentials;
import com.dmtri.common.userio.BasicUserIO;
import com.dmtri.common.usermanagers.AuthCredentials;
import com.dmtri.common.usermanagers.UserManager;

public class LoginCommand extends AbstractCommand {
    private UserManager users;

    public LoginCommand(UserManager users) {
        super("login", false);
        this.users = users;
    }

    @Override
    public RequestBody packageBody(String[] args, BasicUserIO io) throws CommandArgumentException {
        if (args.length > 0) {
            throw new CommandArgumentException(this.getName(), args.length);
        }

        String login = io.read("Username: ");
        io.write("Password: ");
        String password = String.valueOf(System.console().readPassword());

        return new RequestBody(new String[] {login, password});
    }

    @Override
    public Response execute(Request request) throws InvalidRequestException {
        if (request.getBody().getArgsLength() != 2) {
            throw new InvalidRequestException("This operation requires exactly two arguments: Login and password", LocaleKeys.INVALID_VALUE);
        }

        AuthCredentials newCredentials = new AuthCredentials(
            request.getBody().getArg(0),
            request.getBody().getArg(1)
        );

        Long currentUserId = users.authenticate(newCredentials);

        if (currentUserId == null) {
            return new Response("Incorrect login or password", "incorrectCredentials", new Object[] {});
        }

        return new ResponseWithAuthCredentials(newCredentials, "Successfully logged in. Your id is " + currentUserId);
    }

    @Override
    public String getUsage() {
        return "starts login process";
    }
}
