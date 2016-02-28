package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.server.handlers.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.Socket;

class ProcessorImpl implements Processor
{
    private static final Logger logger = LogManager.getLogger();

    private final Remote.Factory remoteFactory;
    private final Handler firstHandler;

    @Inject
    public ProcessorImpl(final Remote.Factory remoteFactory,
                         @Handler.Type final Handler firstHandler)
    {
        this.remoteFactory = remoteFactory;
        this.firstHandler = firstHandler;
    }

    @Override
    public void process(final Socket socket)
    {
        final Remote remote = remoteFactory.create(socket);

        try {
            firstHandler.handle(remote);
            socket.close();
        }
        catch (final Exception e) {
            logger.warn(socket.getRemoteSocketAddress() + " error: " +
                    e.getMessage(), e);
        }

        remote.destroy();
        logger.info("{}: closing", socket.getRemoteSocketAddress());
    }
}
