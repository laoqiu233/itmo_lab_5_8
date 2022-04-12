package com.dmtri.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Iterator;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.network.ObjectSocketWrapper;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.server.collectionmanagers.SaveableCollectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInstance.class);
    private static final int SOCKET_TIMEOUT = 10;
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
                        LOGGER.info("Request from " + client.getSocket().getRemoteSocketAddress() + " for command \"" + request.getCommandName() + '"');
                        Response response = ch.handleRequest(request);
                        client.sendMessage(response);
                        LOGGER.info("Sent response for " + client.getSocket().getRemoteSocketAddress());
                    } else {
                        LOGGER.warn("Received invalid request from " + client.getSocket().getRemoteSocketAddress());
                    }

                    client.clearInBuffer();
                }
            } catch (IOException e) {
                LOGGER.info("Client  " + client.getSocket().getRemoteSocketAddress() + " has been disconnected");
                client.getSocket().close();
                it.remove();
            }
        }
    }

    public void run(int port) throws IOException {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setSoTimeout(SOCKET_TIMEOUT);

            LOGGER.info("Server is listening on port " + port);

            while (true) {
                // Accept input from console and stop server if needed
                if (acceptConsoleInput()) {
                    return;
                }

                // Accept pending connections
                try {
                    while (true) {
                        Socket newClient = socket.accept();
                        newClient.setSoTimeout(SOCKET_TIMEOUT);
                        LOGGER.info("Received connection from " + newClient.getRemoteSocketAddress());
                        clients.add(new ObjectSocketWrapper(newClient));
                    }
                } catch (SocketTimeoutException e) {
                    LOGGER.trace("No more pending connections");
                }

                // Handle new requests
                handleRequests();
            }
        }
    }
}
