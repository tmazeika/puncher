package me.mazeika.transhift.puncher.server;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import me.mazeika.transhift.puncher.binding_annotations.BindAddress;
import me.mazeika.transhift.puncher.binding_annotations.Sockets;

import javax.inject.Inject;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class Acceptor extends AbstractExecutionThreadService
{
    private final SocketAddress bindAddress;
    private final Consumer<SocketChannel> consumer;

    private ServerSocketChannel ch;

    @Inject
    public Acceptor(@BindAddress SocketAddress bindAddress,
                    @Sockets Consumer<SocketChannel> consumer)
    {
        this.bindAddress = bindAddress;
        this.consumer = consumer;
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
            consumer.accept(ch.accept());
        }
    }

    @Override
    protected void shutDown() throws IOException
    {
        ch.close();
    }
}
