package me.mazeika.transhift.puncher.server;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.BindingAnnotation;

import javax.inject.Inject;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class Processor extends AbstractExecutionThreadService
{
    private final Queue<SocketChannel> queue;

    @Inject
    public Processor(@Server.Sockets Queue<SocketChannel> queue)
    {
        this.queue = queue;
    }

    @Override
    protected void run() throws Exception
    {

    }

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public @interface Bind { }
}
