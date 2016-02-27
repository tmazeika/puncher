package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

class TagConsumptionHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    private final Tag.Factory tagFactory;

    @Inject
    public TagConsumptionHandler(final Tag.Factory tagFactory)
    {
        this.tagFactory = tagFactory;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final Tag tag = tagFactory.create(remote.waitAndRead(Tag.LENGTH));

        remote.setTag(tag);

        logger.debug("{}: received tag {}", remote, tag.toString());
    }
}
