package com.dmtri.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.CommandNotFoundException;
import com.dmtri.common.network.ObjectSocketChannelWrapper;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithException;
import com.dmtri.common.network.ResponseWithRoutes;
import com.dmtri.common.userio.BasicUserIO;
import com.dmtri.common.util.TerminalColors;

public class ConsoleClient {
    private static final int TIMEOUT = 10;
    private static final int MILLIS_IN_SECONDS = 1000;
    private BasicUserIO io;
    private CommandHandler ch;
    private String inputPrefix = "> ";
    private ObjectSocketChannelWrapper remote;
    private InetSocketAddress addr;

    public ConsoleClient(InetSocketAddress addr) throws IOException {
        this.io = new BasicUserIO();
        this.ch = CommandHandler.standardCommandHandler(null);
        this.addr = addr;
    }

    private void writeTrace(Exception e) {
        Throwable t = e;

        while (t != null) {
            io.writeln(TerminalColors.colorString(t.toString(), TerminalColors.RED));
            t = t.getCause();
        }

        io.writeln("Use "
                + TerminalColors.colorString("help [command name]", TerminalColors.GREEN)
                + " to get more information on usage of commands"
        );
    }

    private Response waitForResponse() throws IOException {
        int seconds = 0;
        long start = System.currentTimeMillis();

        while (seconds < TIMEOUT) {
            if (remote.checkForMessage()) {
                Object received = remote.getPayload();

                if (received != null && received instanceof Response) {
                    return (Response) received;
                } else {
                    io.writeln("Received invalid response from server");
                    break;
                }
            }

            if (System.currentTimeMillis() >= start + (seconds + 1) * MILLIS_IN_SECONDS) {
                io.write('.');
                seconds++;
            }
        }

        io.writeln("Timed out after " + TIMEOUT + " seconds.");
        return null;
    }

    private void handleResponse(Response response) {
        io.writeln(response.getMessage());

        if (response instanceof ResponseWithRoutes) {
            ResponseWithRoutes rwr = (ResponseWithRoutes) response;

            for (int i = 0; i < rwr.getRoutesCount(); i++) {
                io.writeln(rwr.getRoute(i));
            }
        } else if (response instanceof ResponseWithException) {
            ResponseWithException rwe = (ResponseWithException) response;

            writeTrace(rwe.getException());
        }
    }

    private void inputCycle() {
        String input;
        while ((input = io.read(inputPrefix)) != null) {
            try {
                Request request = ch.handleString(input, io);

                // If the command is not only client-side
                if (request != null) {
                    remote.sendMessage(request);
                    // Block until received response or timed out
                    Response response = waitForResponse();

                    if (response != null) {
                        handleResponse(response);
                    } else {
                        io.writeln("Request failed");
                    }

                    remote.clearInBuffer();
                }
            } catch (
                CommandNotFoundException
                | CommandArgumentException e
            ) {
                writeTrace(e);
            } catch (IOException e) {
                io.writeln("Caught exception when trying to send request");
                writeTrace(e);
                io.writeln("Stopping...");
                return;
            }
        }
    }

    public void run() throws IOException {
        try (SocketChannel socket = SocketChannel.open()) {
            socket.connect(addr);
            socket.configureBlocking(false);
            remote = new ObjectSocketChannelWrapper(socket);

            inputCycle();
        }
    }
}
