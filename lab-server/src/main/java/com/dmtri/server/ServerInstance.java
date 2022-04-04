package com.dmtri.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import com.dmtri.common.CommandHandler;
import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.network.Response;

public class ServerInstance {
    private RequestReceiver channel;
    private CommandHandler ch;

    public ServerInstance(int port, CollectionManager cm) throws IOException {
        DatagramChannel dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        channel = new RequestReceiver(dc);
        ch = CommandHandler.standardCommandHandler(cm);
    }

    public void run() {
        while (true) {
            try {
                RequestReceiver.IncomingRequest incoming = channel.receive();
                if (incoming != null) {
                    Response response = ch.handleRequest(incoming.getRequest());
                    incoming.answer(response);
                }
            } catch (IOException e) {
                System.out.println("Caught exception when trying to process request");
                System.out.println(e);
            }
        }
    }
}
