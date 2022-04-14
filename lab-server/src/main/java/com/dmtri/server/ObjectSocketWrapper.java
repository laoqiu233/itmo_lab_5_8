package com.dmtri.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import com.dmtri.common.network.ObjectEncoder;

public class ObjectSocketWrapper {
    private final Socket socket;
    private byte[] sizeInBuffer;
    private byte[] payloadBuffer;
    private int sizeInBufferPos = 0;
    private int payloadBufferPos = 0;

    public ObjectSocketWrapper(Socket socket) {
        this.socket = socket;
        this.sizeInBuffer = new byte[Integer.BYTES];
        this.payloadBuffer = null;
    }

    public void sendMessage(Object object) throws IOException {
        byte[] msg = ObjectEncoder.encodeObject(object).array();

        socket.getOutputStream().write(msg);
    }

    public boolean checkForMessage() throws IOException {
        try {
            if (payloadBuffer != null && payloadBufferPos >= payloadBuffer.length) {
                return true;
            }

            int readBytes = socket.getInputStream().read(sizeInBuffer, sizeInBufferPos, Integer.BYTES - sizeInBufferPos);
            sizeInBufferPos += readBytes;
            if (sizeInBufferPos < Integer.BYTES) {
                return false;
            }

            if (payloadBuffer == null) {
                payloadBuffer = new byte[ByteBuffer.wrap(sizeInBuffer).getInt()];
            }

            readBytes = socket.getInputStream().read(payloadBuffer, payloadBufferPos, payloadBuffer.length - payloadBufferPos);
            payloadBufferPos += readBytes;

            return payloadBufferPos >= payloadBuffer.length;
        } catch (SocketTimeoutException e) {
            return false;
        }
    }

    public Object getPayload() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payloadBuffer);
        ObjectInputStream ois = new ObjectInputStream(bais);

        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void clearInBuffer() {
        sizeInBuffer = new byte[Integer.BYTES];
        payloadBuffer = null;
        sizeInBufferPos = 0;
        payloadBufferPos = 0;
    }

    public Socket getSocket() {
        return socket;
    }
}
