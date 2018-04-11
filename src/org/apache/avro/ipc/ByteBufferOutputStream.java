package org.apache.avro.ipc;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/** Utility to collect data written to an {@link OutputStream} in {@link
 * ByteBuffer}s.*/
public class ByteBufferOutputStream extends OutputStream {
    public static final int BUFFER_SIZE = 8192;

    private List<ByteBuffer> buffers;

    public ByteBufferOutputStream() {
        reset();
    }

    /** Returns all data written and resets the stream to be empty. */
    public List<ByteBuffer> getBufferList() {
        List<ByteBuffer> result = buffers;
        reset();
        for (ByteBuffer buffer : result) buffer.flip();
        return result;
    }

    public void reset() {
        buffers = new ArrayList<ByteBuffer>(1);
        buffers.add(ByteBuffer.allocate(BUFFER_SIZE));
    }

    public void write(ByteBuffer buffer) {
        buffers.add(buffer);
    }

    public void write(int b) {
        ByteBuffer buffer = buffers.get(buffers.size()-1);
        if (buffer.remaining() < 1) {
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            buffers.add(buffer);
        }
        buffer.put((byte)b);
    }

    public void write(byte b[], int off, int len) {
        ByteBuffer buffer = buffers.get(buffers.size()-1);
        int remaining = buffer.remaining();
        while (len > remaining) {
            buffer.put(b, off, remaining);
            len -= remaining;
            off += remaining;
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            buffers.add(buffer);
            remaining = buffer.remaining();
        }
        buffer.put(b, off, len);
    }

    /** Add a buffer to the output without copying, if possible.
     * Sets buffer's position to its limit.
     */
    public void writeBuffer(ByteBuffer buffer) throws IOException {
        if (buffer.remaining() < BUFFER_SIZE) {
            write(buffer.array(), buffer.position(), buffer.remaining());
        } else {
            buffers.add(buffer);                        // append w/o copying
        }
        buffer.position(buffer.limit());              // mark data as consumed
    }
}
