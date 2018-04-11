package org.apache.avro.ipc;

import java.io.*;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A simple socket-based {@link Transceiver} implementation. */
public class SocketTransceiver extends Transceiver {
    private static final Logger LOG
            = LoggerFactory.getLogger(SocketTransceiver.class);

    private SocketChannel channel;
    private ByteBuffer header = ByteBuffer.allocate(4);

    public String getRemoteName() {
        return channel.socket().getRemoteSocketAddress().toString();
    }

    public SocketTransceiver(SocketAddress address) throws IOException {
        this(SocketChannel.open(address));
    }

    public SocketTransceiver(SocketChannel channel) {
        this.channel = channel;
        LOG.info("open to "+channel.socket().getRemoteSocketAddress());
    }

    public synchronized List<ByteBuffer> readBuffers() throws IOException {
        List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
        while (true) {
            header.clear();
            while (header.hasRemaining()) {
                channel.read(header);
            }
            header.flip();
            int length = header.getInt();
            if (length == 0) {                       // end of buffers
                return buffers;
            }
            ByteBuffer buffer = ByteBuffer.allocate(length);
            while (buffer.hasRemaining()) {
                channel.read(buffer);
            }
            buffer.flip();
            buffers.add(buffer);
        }
    }

    public synchronized void writeBuffers(List<ByteBuffer> buffers)
            throws IOException {
        for (ByteBuffer buffer : buffers) {
            writeLength(buffer.limit());                // length-prefix
            channel.write(buffer);
        }
        writeLength(0);                               // null-terminate
    }

    private void writeLength(int length) throws IOException {
        header.clear();
        header.putInt(length);
        header.flip();
        channel.write(header);
    }

    public void close() throws IOException {
        LOG.info("closing to "+channel.socket().getRemoteSocketAddress());
        channel.close();
    }

}
