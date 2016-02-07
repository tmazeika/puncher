package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.cli.CliModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.*;

public class Listener extends LinkedBlockingQueue<Channel> implements IListener
{
    private static final Logger logger = LogManager.getLogger();

    private final Executor thread = Executors.newSingleThreadExecutor();
    private final CliModel cli;

    @Inject
    public Listener(CliModel cli)
    {
        this.cli = cli;
    }

    @Override
    public void listen() throws IOException
    {
        final ServerSocketChannel server = ServerSocketChannel.open();

        server.socket().bind(
                new InetSocketAddress(cli.getHost(), cli.getPort()));

        thread.execute(() -> {

        });
    }
}
