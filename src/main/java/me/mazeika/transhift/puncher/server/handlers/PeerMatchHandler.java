package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.protocol.Protocol;
import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class PeerMatchHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void handle(final Remote remote) throws Exception
    {
        // if implemented correctly, cannot throw NPE
        final Remote peer = remote.meta().get(MetaKeys.PEER).get();

        // tell target the peer address
        remote.out().write(Protocol.PEER_FOUND);
        remote.out().write(
                peer.socket().getRemoteSocketAddress().toString().getBytes());
        remote.out().write('\n');

        // tell source this remote's address
        peer.out().write(Protocol.PEER_FOUND);
        peer.out().write(
                remote.socket().getRemoteSocketAddress().toString().getBytes());
        peer.out().write('\n');

        logger.debug("{} & {}: addresses exchanged", remote, peer);
    }
}
