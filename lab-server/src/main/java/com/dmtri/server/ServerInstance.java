package com.dmtri.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.network.ObjectSocketWrapper;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.server.collectionmanagers.SaveableCollectionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerInstance {
    private static final Logger LOGGER = LogManager.getLogger(ServerInstance.class);
    private CommandHandler ch;
    private CollectionManager cm;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final HashSet<ObjectSocketWrapper> clients;

    public ServerInstance(CollectionManager cm) throws IOException {
        clients = new HashSet<>();
        ch = CommandHandler.standardCommandHandler(cm);
        this.cm = cm;
    }

    private boolean acceptConsoleInput() throws IOException {
        if (System.in.available() > 0) {
            String command = in.readLine();
            switch (command) {
                case "save":
                    if (cm instanceof SaveableCollectionManager) {
                        ((SaveableCollectionManager) cm).save();
                        System.out.println("Collection saved");
                    } else {
                        System.out.println("The current collection is not manually saveable");
                    }
                    break;
                case "exit":
                    System.out.println("Shutting down");
                    return true;
                default:
                    System.out.println("Unknown command. Available commands are: save, exit");
            }
        }

        return false;
    }

    public void handleRequests() throws IOException {
        Iterator<ObjectSocketWrapper> it = clients.iterator();
        while (it.hasNext()) {
            ObjectSocketWrapper client = it.next();

            try {
                if (client.checkForMessage()) {
                    Object received = client.getPayload();

                    if (received != null && received instanceof Request) {
                        Request request = (Request) received;
                        LOGGER.info("Request from " + client.getSocket().getRemoteAddress() + " for command \"" + request.getCommandName() + '"');
                        Response response = ch.handleRequest(request);
                        client.sendMessage(response);
                        LOGGER.info("Sent response for " + client.getSocket().getRemoteAddress());
                    } else {
                        LOGGER.warn("Received invalid request from " + client.getSocket().getRemoteAddress());
                    }

                    client.clearInBuffer();
                }
            } catch (IOException e) {
                LOGGER.info("Client  " + client.getSocket().getRemoteAddress() + " has been disconnected");
                client.getSocket().close();
                it.remove();
            }
        }
    }

    public void run(int port) throws IOException {
        try (ServerSocketChannel channel = ServerSocketChannel.open();) {
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);

            LOGGER.info("Server is listening on port " + port);

            while (true) {
                // Accept input from console and stop server if needed
                if (acceptConsoleInput()) {
                    return;
                }

                // Accept pending connections
                SocketChannel newClient = null;
                while ((newClient = channel.accept()) != null) {
                    newClient.configureBlocking(false);
                    clients.add(new ObjectSocketWrapper(newClient));
                    LOGGER.info("Received connection from " + newClient.getRemoteAddress());
                }

                // Handle new requests
                handleRequests();
            }
        }
    }
}
