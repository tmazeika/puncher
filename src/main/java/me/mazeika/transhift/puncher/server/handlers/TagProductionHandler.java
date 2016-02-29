package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import me.mazeika.transhift.puncher.tags.Tag;
import me.mazeika.transhift.puncher.tags.TagPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class TagProductionHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    private final TagPool tagPool;
    private final Provider<Handler> blockHandlerProvider;

    @Inject
    public TagProductionHandler(final TagPool tagPool,
                                @Block final Provider<Handler>
                                        blockHandlerProvider)
    {
        this.tagPool = tagPool;
        this.blockHandlerProvider = blockHandlerProvider;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final Tag tag = tagPool.generate();

        remote.meta().set(MetaKeys.TAG, tag);

        // send the generated tag to the remote
        remote.out().write(tag.get());
        remote.out().flush();

        logger.debug("{}: wrote tag {}", remote, tag);
        blockHandlerProvider.get().handle(remote);
    }
}
