package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.protocol.Protocol;
import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import me.mazeika.transhift.puncher.tags.Tag;
import me.mazeika.transhift.puncher.tags.TagPool;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Optional;

public class TagSearchHandler implements Handler
{
    private final Collection<Remote> globalRemotePool;
    private final TagPool tagPool;
    private final Provider<Handler> peerMatchHandlerProvider;

    @Inject
    public TagSearchHandler(
            @Remote.Pool final Collection<Remote> globalRemotePool,
            final TagPool tagPool, @AddressExchange final Provider<Handler>
                    peerMatchHandlerProvider)
    {
        this.globalRemotePool = globalRemotePool;
        this.tagPool = tagPool;
        this.peerMatchHandlerProvider = peerMatchHandlerProvider;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        // if implemented correctly, cannot throw NPE
        final Tag tag = remote.meta().get(MetaKeys.TAG).get();

        final Optional<Remote> peerRemoteOp = globalRemotePool.parallelStream()
                .filter(e -> e.meta().get(MetaKeys.TAG).isPresent())
                .filter(e -> e.meta().get(MetaKeys.TAG).get().equals(tag))
                .findAny();

        tagPool.remove(tag);

        if (peerRemoteOp.isPresent()) {
            remote.meta().set(MetaKeys.PEER, peerRemoteOp.get());
            peerMatchHandlerProvider.get().handle(remote);
        }
        else {
            remote.out().write(Protocol.PEER_NOT_FOUND);
        }
    }
}
