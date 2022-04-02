package com.dmtri.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
    private static final int BUFFERSIZE = 1024;
    private BasicUserIO io;
    private CommandHandler ch;
    private String inputPrefix = "> ";
    private DatagramChannel dc;

    public ConsoleClient() {
        this.io = new BasicUserIO();
        this.ch = CommandHandler.standardCommandHandler(null);
    }

    public void writeTrace(Exception e) {
        io.writeln(TerminalColors.colorString(e.toString(), TerminalColors.RED));

        Throwable t = e.getCause();

        while (t != null) {
            io.writeln(TerminalColors.colorString(t.toString(), TerminalColors.RED));
            t = t.getCause();
        }

        io.writeln("Use "
                + TerminalColors.colorString("help [command name]", TerminalColors.GREEN)
                + " to get more information on usage of commands"
        );
    }

    private void createConnection(String address, int port) throws IOException {
        dc = DatagramChannel.open();
        dc.connect(new InetSocketAddress(address, port));
    }

    private Response sendRequest(Request request) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(request);
            dc.send(ByteBuffer.wrap(baos.toByteArray()), dc.getRemoteAddress());

            byte[] arr = new byte[BUFFERSIZE];
            ByteBuffer buffer = ByteBuffer.wrap(arr);

            dc.receive(buffer);

            ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object obj = ois.readObject();

            if (obj instanceof Response) {
                return (Response) obj;
            } else {
                io.writeln("The server responded with an invalid object");
            }
        } catch (IOException e) {
            writeTrace(e);
        } catch (ClassNotFoundException e) {
            io.writeln("The server responded with an invalid object (Class not found)");
            writeTrace(e);
        }

        return null;
    }

    private void inputCycle() {
        String input;
        while ((input = io.read(inputPrefix)) != null) {
            try {
                Request request = ch.handle(input, io);

                if (request != null) {
                    Response response = sendRequest(request);

                    io.writeln(response.getMessage());

                    if (response instanceof ResponseWithException) {
                        ResponseWithException rwe = (ResponseWithException) response;
                        writeTrace(rwe.getException());
                    } else if (response instanceof ResponseWithRoutes) {
                        ResponseWithRoutes rwr = (ResponseWithRoutes) response;

                        for (int i = 0; i < rwr.getRoutesCount(); i++) {
                            io.writeln(rwr.getRoute(i));
                        }
                    }
                }
            } catch (
                CommandNotFoundException
                | CommandArgumentException e
            ) {
                writeTrace(e);
            }
        }
    }

    public void run() {
        String address = io.read("Enter host address: ");
        int port = Integer.valueOf(io.read("Enter port: "));

        try {
            createConnection(address, port);
        } catch (IOException e) {
            writeTrace(e);
            return;
        }

        inputCycle();
    }
}
