package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.pipeline.Pipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ServerImpl implements Server
{
    private static final Logger logger = LogManager.getLogger();
    private static final ExecutorService exec = Executors.newCachedThreadPool();

    private final Acceptor acceptor;
    private final Provider<Processor> processorProvider;
    private final Pipeline shutdownPipeline;

    private volatile boolean running = true;
    private Thread thread;

    @Inject
    public ServerImpl(final Acceptor acceptor,
                      final Provider<Processor> processorProvider,
                      @Pipeline.Shutdown Pipeline shutdownPipeline)
    {
        this.acceptor = acceptor;
        this.processorProvider = processorProvider;
        this.shutdownPipeline = shutdownPipeline;
    }

    @Override
    public void start() throws IOException
    {
        final BlockingQueue<Socket> queue = acceptor.accept();
        thread = Thread.currentThread();

        // add shutdown hook
        shutdownPipeline.register(this::stop);

        while (running) {
            try {
                final Socket socket = queue.take();

                exec.execute(() -> {
                    try {
                        logger.debug("{}: processing",
                                socket.getRemoteSocketAddress());
                        processorProvider.get().process(socket);
                    }
                    catch (Exception ignored) { }
                    finally {
                        try {
                            socket.close();
                        }
                        catch (IOException ignored) {
                        }
                    }

                    logger.info("{}: closing", socket.getRemoteSocketAddress());
                });
            }
            catch (InterruptedException ignored) { }
        }
    }

    private void stop()
    {
        running = false;

        thread.interrupt();
        exec.shutdownNow();
    }
}
