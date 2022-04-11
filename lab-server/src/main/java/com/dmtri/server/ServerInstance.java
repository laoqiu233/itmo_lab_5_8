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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerInstance {
    private static final Logger LOGGER = LogManager.getLogger(ServerInstance.class);
    private CommandHandler ch;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final HashSet<ObjectSocketWrapper> clients;

    public ServerInstance(CollectionManager cm) throws IOException {
        clients = new HashSet<>();
        ch = CommandHandler.standardCommandHandler(cm);
    }

    public void run(int port) throws IOException {
        try (ServerSocketChannel channel = ServerSocketChannel.open();) {
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);

            LOGGER.info("Server is listening on port " + port);

            while (true) {
                // Accept pending connections
                SocketChannel newClient = null;
                while ((newClient = channel.accept()) != null) {
                    clients.add(new ObjectSocketWrapper(newClient));
                    LOGGER.info("Received connection from " + newClient.getRemoteAddress());
                }

                // Handle new requests
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
        }
    }
}
