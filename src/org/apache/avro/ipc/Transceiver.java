package org.apache.avro.ipc;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/** Base class for transmitters and recievers of raw binary messages. */
public abstract class Transceiver {

    public abstract String getRemoteName();

    public synchronized List<ByteBuffer> transceive(List<ByteBuffer> request)
            throws IOException {
        writeBuffers(request);
        return readBuffers();
    }

    public abstract List<ByteBuffer> readBuffers() throws IOException;

    public abstract void writeBuffers(List<ByteBuffer> buffers)
            throws IOException;

    public void close() throws IOException {}
}
