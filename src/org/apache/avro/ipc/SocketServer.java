package org.apache.avro.ipc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A simple socket-based server implementation. */
public class SocketServer extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(SocketServer.class);

    private Responder responder;
    private ServerSocketChannel channel;
    private ThreadGroup group;

    public SocketServer(Responder responder, SocketAddress addr)
            throws IOException {
        String name = "SocketServer on "+addr;

        this.responder = responder;
        this.group = new ThreadGroup(name);
        this.channel = ServerSocketChannel.open();

        channel.socket().bind(addr);

        setName(name);
        setDaemon(true);
        start();
    }

    public int getPort() { return channel.socket().getLocalPort(); }

    public void run() {
        LOG.info("starting "+channel.socket().getInetAddress());
        while (true) {
            try {
                new Connection(channel.accept());
            } catch (ClosedChannelException e) {
                return;
            } catch (IOException e) {
                LOG.warn("unexpected error", e);
                throw new RuntimeException(e);
            } finally {
                LOG.info("stopping "+channel.socket().getInetAddress());
            }
        }
    }

    public void close() {
        group.interrupt();
    }

    private class Connection extends SocketTransceiver implements Runnable {

        public Connection(SocketChannel channel) {
            super(channel);

            Thread thread = new Thread(group, this);
            thread.setName("Connection to "+channel.socket().getRemoteSocketAddress());
            thread.setDaemon(true);
            thread.start();
        }

        public void run() {
            try {
                try {
                    while (true) {
                        writeBuffers(responder.respond(this));
                    }
                } catch (ClosedChannelException e) {
                    return;
                } finally {
                    close();
                }
            } catch (IOException e) {
                LOG.warn("unexpected error", e);
            }
        }

    }

    public static void main(String arg[]) throws Exception {
        SocketServer server = new SocketServer(null, new InetSocketAddress(0));
        System.out.println("started");
        server.join();
    }
}
