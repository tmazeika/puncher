package me.mazeika.transhift.puncher.server;

import com.google.common.util.concurrent.Service;

import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public interface AcceptorFactory
{
    @Acceptor.Bind
    Service createAcceptor(Consumer<SocketChannel> consumer);
}
