package com.dmtri.common.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ObjectSocketWrapper {
    private final SocketChannel socket;
    private ByteBuffer sizeIntBuffer = ByteBuffer.allocate(Integer.BYTES);
    private ByteBuffer payloadBuffer = null;

    public ObjectSocketWrapper(SocketChannel socket) {
        this.socket = socket;
    }

    /**
     * Serializes and encodes an object with the following format:
     * [4 bytes integer N = size of the serialized object][ N bytes of the serialized object ]
     * @param object
     * @return a byte buffer with the above specfied format.
     */
    public static ByteBuffer encodeObject(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + baos.size());

        buffer.putInt(baos.size());
        buffer.put(baos.toByteArray());

        return buffer;
    }

    public void sendMessage(Object object) throws IOException {
        ByteBuffer outBuffer = encodeObject(object);
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
