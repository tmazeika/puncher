package me.mazeika.transhift.puncher.server;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.BindingAnnotation;
import me.mazeika.transhift.puncher.binding_annotations.BindAddress;
import me.mazeika.transhift.puncher.binding_annotations.Incoming;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class Acceptor extends AbstractExecutionThreadService
{
    private final SocketAddress bindTo;
    private final Queue<SocketChannel> queue;

    private ServerSocketChannel ch;

    @Inject
    public Acceptor(@BindAddress SocketAddress bindTo,
                    @Incoming Queue<SocketChannel> queue)
    {
        this.bindTo = bindTo;
        this.queue = queue;
    }

    @Override
    protected void startUp() throws IOException
    {
        ch = ServerSocketChannel.open();
        ch.bind(bindTo);
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

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public @interface Bind { }
}
