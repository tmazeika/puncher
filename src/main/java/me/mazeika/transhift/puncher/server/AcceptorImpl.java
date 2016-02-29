package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.options.Options;
import me.mazeika.transhift.puncher.pipeline.Pipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class AcceptorImpl implements Acceptor
{
    private static final int BACKLOG = 128;
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

        createSslServer();
        logger.info("listening @ {}", serverSocket.getLocalSocketAddress());

        // add shutdown hook
        shutdownPipeline.register(this::stop);

        exec.execute(() -> {
            while (running) {
                try {
                    final Socket remote = serverSocket.accept();

                    remote.setKeepAlive(true);
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

    private void createSslServer() throws IOException
    {
        final SSLServerSocketFactory factory =
                (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        serverSocket = factory.createServerSocket(options.port(), BACKLOG,
                InetAddress.getByName(options.host()));
    }
}
