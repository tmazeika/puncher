package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;

public interface Handler
{
    void handle(Remote remote) throws Exception;
}
