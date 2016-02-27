package me.mazeika.transhift.puncher.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public interface Acceptor
{
    /**
     * Begins the acceptor thread, returning immediately, where the returned
     * {@link BlockingQueue} will contain incoming sockets.
     *
     * @return the queue of sockets
     *
     * @throws IOException
     */
    BlockingQueue<Socket> accept() throws IOException;
}
