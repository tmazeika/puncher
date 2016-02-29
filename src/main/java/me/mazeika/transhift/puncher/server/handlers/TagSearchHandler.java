package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.protocol.Protocol;
import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.SourceConnectNotifier;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import me.mazeika.transhift.puncher.tags.Tag;
import me.mazeika.transhift.puncher.tags.TagPool;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

class TagSearchHandler implements Handler
{
    private final SourceConnectNotifier notifier;
    private final TagPool tagPool;
    private final Provider<Handler> peerMatchHandlerProvider;

    @Inject
    public TagSearchHandler(final SourceConnectNotifier notifier,
                            final TagPool tagPool,
                            @AddressExchange final Provider<Handler>
                                    peerMatchHandlerProvider)
    {
        this.notifier = notifier;
        this.tagPool = tagPool;
        this.peerMatchHandlerProvider = peerMatchHandlerProvider;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final Tag tag = remote.meta().get(MetaKeys.TAG).get();
        final Optional<Remote> peerOp = notifier.fire(remote, tag);

        tagPool.remove(tag);

        if (peerOp.isPresent()) {
            remote.meta().set(MetaKeys.PEER, peerOp.get());
            peerMatchHandlerProvider.get().handle(remote);
        }
        else {
            remote.out().write(Protocol.PEER_NOT_FOUND);
            remote.out().flush();
        }
    }
}
