package me.mazeika.transhift.puncher.server;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import me.mazeika.transhift.puncher.binding_annotations.Sockets;

import javax.inject.Inject;
import java.nio.channels.SocketChannel;
import java.util.Queue;

public class Processor extends AbstractExecutionThreadService
{
    private final Queue<SocketChannel> queue;

    @Inject
    public Processor(@Sockets Queue<SocketChannel> queue)
    {
        this.queue = queue;
    }

    @Override
    protected void run() throws Exception
    {

    }
}
