package com.dmtri.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.dmtri.common.network.ObjectEncoder;

public class ObjectSocketChannelWrapper {
    private final SocketChannel socket;
    private ByteBuffer sizeIntBuffer = ByteBuffer.allocate(Integer.BYTES);
    private ByteBuffer payloadBuffer = null;

    public ObjectSocketChannelWrapper(SocketChannel socket) {
        this.socket = socket;
    }

    public void sendMessage(Object object) throws IOException {
        ByteBuffer outBuffer = ObjectEncoder.encodeObject(object);
        outBuffer.flip();

        while (outBuffer.hasRemaining()) {
            socket.write(outBuffer);
        }
    }

    public boolean checkForMessage() throws IOException {
        // No need to check anything if payload is already read.
        if (payloadBuffer != null && !payloadBuffer.hasRemaining()) {
            return true;
        }

        // Try to read the entire header containing number of bytes in payload
        socket.read(sizeIntBuffer);
        if (sizeIntBuffer.hasRemaining()) {
            return false;
        }

        // Header is received, generate the payload buffer
        if (payloadBuffer == null) {
            payloadBuffer = ByteBuffer.allocate(sizeIntBuffer.getInt(0));
        }

        // Try to read to payload buffer
        socket.read(payloadBuffer);
        return !payloadBuffer.hasRemaining();
    }

    public Object getPayload() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payloadBuffer.array());
        ObjectInputStream ois = new ObjectInputStream(bais);

        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void clearInBuffer() {
        sizeIntBuffer.clear();
        payloadBuffer = null;
    }

    public SocketChannel getSocket() {
        return socket;
    }
}
