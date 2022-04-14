package com.dmtri.common.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public final class ObjectEncoder {
    private ObjectEncoder() {
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

}
