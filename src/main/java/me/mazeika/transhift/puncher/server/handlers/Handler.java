package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;

public interface Handler
{
    /**
     * Handles a {@link Remote}.
     *
     * @param remote the remote
     *
     * @throws Exception
     */
    void handle(Remote remote) throws Exception;
}
