package me.mazeika.transhift.puncher.server;

public interface Server
{
    /**
     * Starts the server, blocking until it shuts down.
     *
     * @throws Exception when an exception occurred creating or during operation
     * of the server
     */
    void start() throws Exception;
}
