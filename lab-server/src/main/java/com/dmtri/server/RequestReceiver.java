package com.dmtri.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestReceiver {
    private static final int BUFFERSIZE = 2048;
    private static final Logger LOGGER = LogManager.getLogger(RequestReceiver.class);
    private DatagramChannel dc;

    public RequestReceiver(DatagramChannel dc) {
        this.dc = dc;
    }

    public IncomingRequest receive() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
        SocketAddress sender = dc.receive(buffer);

        if (sender != null) {
            LOGGER.info("Received packet from " + sender);

            ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
            ObjectInputStream ois = new ObjectInputStream(bais);

            try {
                Object obj = ois.readObject();

                if (obj instanceof Request) {
                    Request request = (Request) obj;
                    return new IncomingRequest(request, sender);
                }

                LOGGER.warn("Received invalid request object");
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Received invalid request object", e);
            }
        }

        return null;
    }

    public class IncomingRequest {
        private Request request;
        private SocketAddress sender;

        public IncomingRequest(Request request, SocketAddress sender) {
            this.request = request;
            this.sender = sender;
        }

        public void answer(Response response) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(response);

            dc.send(ByteBuffer.wrap(baos.toByteArray()), sender);
            LOGGER.trace("Responded to client " + sender + " on command \"" + request.getCommandName() + '"');
        }

        public Request getRequest() {
            return request;
        }

        public SocketAddress getSender() {
            return sender;
        }
    }
}
