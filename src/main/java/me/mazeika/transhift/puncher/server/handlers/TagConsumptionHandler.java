package me.mazeika.transhift.puncher.server.handlers;

import com.google.common.io.ByteStreams;
import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import me.mazeika.transhift.puncher.tags.Tag;
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
                                 @TagSearch final Provider<Handler>
                                         tagSearchHandlerProvider)
    {
        this.tagFactory = tagFactory;
        this.tagSearchHandlerProvider = tagSearchHandlerProvider;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final byte[] tagBytes = new byte[Tag.LENGTH];

        ByteStreams.readFully(remote.in(), tagBytes);

        final Tag tag = tagFactory.create(tagBytes);

        logger.debug("{}: received tag {}", remote, tag.toString());
        remote.meta().set(MetaKeys.TAG, tag);
        tagSearchHandlerProvider.get().handle(remote);
    }
}
