package com.dmtri.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.exceptions.CommandArgumentException;
import com.dmtri.common.exceptions.CommandNotFoundException;
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
    private RequestSender channel;

    public ConsoleClient(String address, int port) throws IOException {
        this.io = new BasicUserIO();
        this.ch = CommandHandler.standardCommandHandler(null);
        DatagramChannel dc = DatagramChannel.open();
        dc.connect(new InetSocketAddress(address, port));
        dc.configureBlocking(false);
        channel = new RequestSender(dc);
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

    private Response waitForResponse(RequestSender.SentRequest request) throws IOException {
        int seconds = 0;
        long start = System.currentTimeMillis();

        while (seconds < TIMEOUT) {
            if (request.checkForResponse()) {
                if (request.isInvalidResponse()) {
                    io.writeln("Received an invalid response");
                    return null;
                } else {
                    return request.getResponse();
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

    private void inputCycle() {
        String input;
        while ((input = io.read(inputPrefix)) != null) {
            try {
                Request request = ch.handleString(input, io);

                if (request != null) {
                    Response response = waitForResponse(channel.sendRequest(request));
                    if (response != null) {
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
                    } else {
                        io.writeln("Request failed");
                    }
                }
            } catch (
                CommandNotFoundException
                | CommandArgumentException e
            ) {
                writeTrace(e);
            } catch (IOException e) {
                io.writeln("Caught exception when trying to send request");
                writeTrace(e);
            }
        }
    }

    public void run() {
        inputCycle();
    }
}
