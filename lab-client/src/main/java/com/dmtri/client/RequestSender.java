package com.dmtri.client;

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

public class RequestSender {
    private static final int BUFFERSIZE = 2048;

    private DatagramChannel dc;

    public RequestSender(DatagramChannel dc) {
        this.dc = dc;
    }

    public SentRequest sendRequest(Request request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(request);
        dc.send(ByteBuffer.wrap(baos.toByteArray()), dc.getRemoteAddress());

        return this.new SentRequest();
    }

    public class SentRequest {
        private Response response = null;
        private boolean invalidResponse = false;

        public Response getResponse() {
            return response;
        }

        public boolean checkForResponse() throws IOException {
            if (response != null || invalidResponse) {
                return true;
            }

            byte[] arr = new byte[BUFFERSIZE];
            ByteBuffer buffer = ByteBuffer.wrap(arr);

            SocketAddress sender = dc.receive(buffer);

            if (sender != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
                ObjectInputStream ois = new ObjectInputStream(bais);

                try {
                    Object obj = ois.readObject();

                    if (obj instanceof Response) {
                        response = (Response) obj;
                    }
                } catch (ClassNotFoundException e) {
                    invalidResponse = true;
                }

                return true;
            }

            return false;
        }

        public boolean isInvalidResponse() {
            return invalidResponse;
        }
    }
}
