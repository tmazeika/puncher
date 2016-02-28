package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;

class TagConsumptionHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    private final Tag.Factory tagFactory;
    private final Provider<Handler> tagSearchHandlerProvider;

    @Inject
    public TagConsumptionHandler(final Tag.Factory tagFactory,
                                 @Handler.Search final Provider<Handler>
                                         tagSearchHandlerProvider)
    {
        this.tagFactory = tagFactory;
        this.tagSearchHandlerProvider = tagSearchHandlerProvider;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final Tag tag = tagFactory.create(remote.waitAndRead(Tag.LENGTH));

        remote.setTag(tag);

        logger.debug("{}: received tag {}", remote, tag.toString());

        tagSearchHandlerProvider.get().handle(remote);
    }
}
