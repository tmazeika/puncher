package me.mazeika.transhift.puncher.server.handlers;

import java.net.Socket;

public interface Handler
{
    void handle(Socket socket) throws Exception;
}
