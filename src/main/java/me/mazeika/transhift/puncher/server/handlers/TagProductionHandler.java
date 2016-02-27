package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TagProductionHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void handle(final Remote remote) throws Exception
    {
        logger.trace("TagProductionHandler#handle");
    }
}
