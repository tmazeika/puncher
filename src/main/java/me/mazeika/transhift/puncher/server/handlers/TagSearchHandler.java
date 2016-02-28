package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.protocol.Protocol;
import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import me.mazeika.transhift.puncher.tags.Tag;
import me.mazeika.transhift.puncher.tags.TagPool;

import javax.inject.Inject;
import java.util.Optional;

public class TagSearchHandler implements Handler
{
    private final TagPool tagPool;
    private final Handler peerMatchHandler;

    @Inject
    public TagSearchHandler(final TagPool tagPool,
                            @Handler.PeerMatch final Handler peerMatchHandler)
    {
        this.tagPool = tagPool;
        this.peerMatchHandler = peerMatchHandler;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        // if implemented correctly, cannot throw NPE
        final Tag tag = remote.meta().get(MetaKeys.TAG).get();

        final Optional<Remote> peerRemoteOp = tagPool.findAndRemove(tag);

        if (peerRemoteOp.isPresent()) {
            remote.meta().set(MetaKeys.PEER, peerRemoteOp.get());
            peerMatchHandler.handle(remote);
        }
        else {
            remote.out().write(Protocol.PEER_NOT_FOUND);
        }
    }
}
