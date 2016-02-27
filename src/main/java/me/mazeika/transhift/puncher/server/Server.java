package me.mazeika.transhift.puncher.server;

import java.io.IOException;

public interface Server
{
    /**
     * Bootstraps and starts the server.
     *
     * @throws IOException
     */
    void start() throws IOException;
}
