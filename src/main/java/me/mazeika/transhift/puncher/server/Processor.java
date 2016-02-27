package me.mazeika.transhift.puncher.server;

import java.io.IOException;
import java.net.Socket;

public interface Processor
{
    /**
     * Processes the given socket.
     *
     * @param socket the socket to process
     *
     * @throws IOException
     */
    void process(Socket socket) throws IOException;
}
