package me.mazeika.transhift.puncher.server;

import java.io.IOException;
import java.net.Socket;

public interface Processor
{
    void process(Socket socket) throws IOException;
}
