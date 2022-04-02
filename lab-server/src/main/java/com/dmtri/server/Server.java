package com.dmtri.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;

public final class Server {
    private static final int BUFFERSIZE = 1024;
    private static final int PORT = 12345;

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramChannel dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(PORT));

        byte[] arr = new byte[BUFFERSIZE];

        while (true) {
            ByteBuffer buffer = ByteBuffer.wrap(arr);
            buffer.clear();
            SocketAddress sender = dc.receive(buffer);
            System.out.println("Received message from " + sender.toString());
            buffer.flip();

            ByteArrayInputStream bais = new ByteArrayInputStream(arr);
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object obj = ois.readObject();

            if (obj instanceof Request) {
                Request request = (Request) obj;
                System.out.println(request.getCommandName());

                buffer.clear();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeObject(new Response(request.getCommandName()));
                buffer = ByteBuffer.wrap(baos.toByteArray());

                dc.send(buffer, sender);
            } else {
                System.out.println("Not a valid request");
            }
        }
    }
}
