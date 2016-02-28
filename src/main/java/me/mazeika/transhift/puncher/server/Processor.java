package me.mazeika.transhift.puncher.server;

import java.net.Socket;

public interface Processor
{
    /**
     * Processes the given socket.
     *
     * @param socket the socket to process
     */
    void process(Socket socket);
}
