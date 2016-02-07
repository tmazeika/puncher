package me.mazeika.transhift.puncher.server;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.concurrent.BlockingQueue;

public interface IListener extends BlockingQueue<Channel>
{
    void listen() throws IOException;
}
