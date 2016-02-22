package me.mazeika.transhift.puncher.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public interface Acceptor
{
    BlockingQueue<Socket> accept() throws IOException;
}
