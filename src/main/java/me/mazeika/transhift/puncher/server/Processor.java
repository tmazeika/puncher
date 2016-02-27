package me.mazeika.transhift.puncher.server;

import java.net.Socket;

public interface Processor
{
    /**
     * Processes the given socket.
     *
     * @param socket the socket to process
     *
     * @throws Exception
     */
    void process(Socket socket) throws Exception;
}
