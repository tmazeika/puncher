package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.options.Options;
import me.mazeika.transhift.puncher.pipeline.Pipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class AcceptorImpl implements Acceptor
{
    private static final Logger logger = LogManager.getLogger();

    private final Executor exec = Executors.newSingleThreadExecutor();
    private final Options options;
    private final Pipeline shutdownPipeline;

    private volatile boolean running = true;
    private ServerSocket serverSocket;

    @Inject
    public AcceptorImpl(final Options options,
                        @Pipeline.Shutdown final Pipeline shutdownPipeline)
    {
        this.options = options;
        this.shutdownPipeline = shutdownPipeline;
    }

    @Override
    public BlockingQueue<Socket> accept() throws IOException
    {
        final BlockingQueue<Socket> queue = new LinkedBlockingQueue<>();
        serverSocket = new ServerSocket();

        serverSocket.bind(new InetSocketAddress(
                options.getHost(), options.getPort()));

        logger.info("listening @ {}", serverSocket.getLocalSocketAddress());

        // add shutdown hook
        shutdownPipeline.register(this::stop);

        exec.execute(() -> {
            while (running) {
                try {
                    final Socket remote = serverSocket.accept();

                    queue.put(remote);
                    logger.info("{}: accepted",
                            remote.getRemoteSocketAddress());
                }
                catch (InterruptedException | IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        });

        return queue;
    }

    private void stop()
    {
        running = false;

        try {
            serverSocket.close();
        }
        catch (IOException ignored) { }
    }
}
