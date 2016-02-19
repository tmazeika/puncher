package me.mazeika.transhift.puncher.server;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import me.mazeika.transhift.puncher.binding_annotations.BindAddress;
import me.mazeika.transhift.puncher.binding_annotations.Sockets;

import javax.inject.Inject;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

public class Acceptor extends AbstractExecutionThreadService
{
    private final SocketAddress bindAddress;
    private final Queue<SocketChannel> queue;

    private ServerSocketChannel ch;

    @Inject
    public Acceptor(@BindAddress SocketAddress bindAddress,
                    @Sockets Queue<SocketChannel> queue)
    {
        this.bindAddress = bindAddress;
        this.queue = queue;
    }

    @Override
    protected void startUp() throws IOException
    {
        ch = ServerSocketChannel.open();
        ch.bind(bindAddress);
    }

    @Override
    protected void run() throws IOException
    {
        while (isRunning()) {
            queue.offer(ch.accept());
        }
    }

    @Override
    protected void shutDown() throws IOException
    {
        ch.close();
    }
}
