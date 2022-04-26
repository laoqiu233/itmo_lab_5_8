package com.dmtri.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithException;
import com.dmtri.common.usermanagers.UserManager;
import com.dmtri.server.collectionmanagers.SaveableCollectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInstance.class);
    private static final int SOCKET_TIMEOUT = 10;
    private final ForkJoinPool requestHandlerPool = new ForkJoinPool();
    private final ExecutorService responseSenderPool = Executors.newCachedThreadPool();
    private CommandHandler ch;
    private CollectionManager cm;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final UserManager users;

    public ServerInstance(CollectionManager cm, UserManager users) throws IOException {
        ch = CommandHandler.standardCommandHandler(cm, users);
        this.cm = cm;
        this.users = users;
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

    public void run(int port) throws IOException {
        Set<ClientThread> clients = new HashSet<>();

        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setSoTimeout(SOCKET_TIMEOUT);

            LOGGER.info("Server is listening on port " + port);

            while (true) {
                // Remove dead threads
                clients.removeIf(x -> !x.isRunning());

                // Accept input from console and stop server if needed
                if (acceptConsoleInput()) {
                    clients.forEach(x -> x.stop());
                    requestHandlerPool.shutdown();
                    responseSenderPool.shutdown();
                    return;
                }

                // Accept pending connections
                try {
                    while (true) {
                        Socket newClient = socket.accept();
                        newClient.setSoTimeout(SOCKET_TIMEOUT);
                        LOGGER.info("Received connection from " + newClient.getRemoteSocketAddress());
                        ClientThread client = new ClientThread(new ObjectSocketWrapper(newClient));
                        clients.add(client);
                        client.start();
                    }
                } catch (SocketTimeoutException e) {
                    LOGGER.trace("No more pending connections");
                }
            }
        }
    }

    private class ClientThread {
        private final ObjectSocketWrapper socket;
        private final Thread thread;
        private boolean running = false;

        ClientThread(ObjectSocketWrapper socket) {
            this.socket = socket;
            this.thread = new Thread(this::handleRequests);
            this.thread.setName("Client" + this.socket.getSocket().getRemoteSocketAddress());
        }

        boolean isRunning() {
            return running;
        }

        void start() {
            thread.start();
            running = true;
        }

        void stop() {
            running = false;
            LOGGER.info("Client  " + socket.getSocket().getRemoteSocketAddress() + " has been disconnected");
            try {
                socket.getSocket().close();
            } catch (IOException e) {
                LOGGER.warn("Failed to close connection with client " + socket.getSocket().getRemoteSocketAddress(), e);
            }
        }

        void handleRequests() {
            while (running) {
                try {
                    if (socket.checkForMessage()) {
                        Object received = socket.getPayload();

                        if (received != null && received instanceof Request) {
                            Request request = (Request) received;
                            LOGGER.info("Request from " + socket.getSocket().getRemoteSocketAddress() + " for command \"" + request.getCommandName() + '"');
                            try {
                                Response response = requestHandlerPool.submit(() -> ch.handleRequest(request, users)).get();
                                responseSenderPool.submit(() -> {
                                    if (!socket.sendMessage(response)) {
                                        LOGGER.error("Failed to send message to client " + this.socket.getSocket().getRemoteSocketAddress());
                                    }
                                });
                            } catch (InterruptedException | ExecutionException e) {
                                socket.sendMessage(new ResponseWithException(e));
                            }
                            LOGGER.info("Sent response for " + socket.getSocket().getRemoteSocketAddress());
                        } else {
                            LOGGER.warn("Received invalid request from " + socket.getSocket().getRemoteSocketAddress());
                        }

                        socket.clearInBuffer();
                    }
                } catch (IOException e) {
                    stop();
                }
            }
        }
    }
}
