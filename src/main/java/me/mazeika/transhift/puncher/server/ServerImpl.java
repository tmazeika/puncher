package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.options.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class ServerImpl implements Server
{
    private static final Logger logger = LogManager.getLogger();

    private final Options options;

    @Inject
    public ServerImpl(final Options options)
    {
        this.options = options;
    }

    @Override
    public void start()
    {
        logger.trace("ServerImpl#start");
    }
}
