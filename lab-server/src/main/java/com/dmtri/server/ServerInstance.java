package com.dmtri.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.commands.AbstractCommand;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.server.collectionmanagers.SaveableCollectionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerInstance {
    private static final Logger LOGGER = LogManager.getLogger(ServerInstance.class);
    private RequestReceiver channel;
    private CommandHandler ch;
    private CollectionManager cm;
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public ServerInstance(int port, CollectionManager cm) throws IOException {
        DatagramChannel dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        dc.configureBlocking(false);
        channel = new RequestReceiver(dc);
        ch = CommandHandler.standardCommandHandler(cm);
        // A private command for clients to verify server is up.
        ch.addCommand(new AbstractCommand("__HEALTH_CHECK__") {
            @Override
            public String getUsage() {
                return "";
            }

            @Override
            public Response execute(Request request) throws InvalidRequestException {
                return new Response("Successfully connected");
            }
        });
        this.cm = cm;

        LOGGER.info("Server is listening on port " + port);
    }

    public void run() throws IOException {
        while (true) {
            try {
                if (System.in.available() > 0) {
                    String command = in.readLine();
                    switch (command) {
                        case "save":
                            if (cm instanceof SaveableCollectionManager) {
                                ((SaveableCollectionManager) cm).save();
                            } else {
                                System.out.println("The current collection is not manually saveable.");
                            }
                            break;
                        case "exit":
                            LOGGER.info("Shutting down...");
                            return;
                        default:
                            System.out.println("Command not found. Available commands: save, exit");
                    }
                }

                RequestReceiver.IncomingRequest incoming = channel.receive();
                if (incoming != null) {
                    LOGGER.trace("Executing command \"" + incoming.getRequest().getCommandName() + "\" for client " + incoming.getSender());
                    Response response = ch.handleRequest(incoming.getRequest());
                    incoming.answer(response);
                }
            } catch (IOException e) {
                LOGGER.error("Caught exception when trying to receive request", e);
            }
        }
    }
}
