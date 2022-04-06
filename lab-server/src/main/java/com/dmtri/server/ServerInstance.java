package com.dmtri.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.network.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerInstance {
    private static final Logger LOGGER = LogManager.getLogger(ServerInstance.class);
    private RequestReceiver channel;
    private CommandHandler ch;

    public ServerInstance(int port, CollectionManager cm) throws IOException {
        DatagramChannel dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        channel = new RequestReceiver(dc);
        ch = CommandHandler.standardCommandHandler(cm);

        LOGGER.info("Server is listening on port " + port);
    }

    public void run() {
        while (true) {
            try {
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
