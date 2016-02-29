package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.protocol.Protocol;
import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AddressExchangeHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final Remote peer = remote.meta().get(MetaKeys.PEER).get();

        remote.out().write(Protocol.PEER_FOUND);
        remote.out().write(
                peer.socket().getRemoteSocketAddress().toString().getBytes());
        remote.out().write('\n');
        remote.out().flush();

        logger.debug("{}: sent address of {}", remote, peer);
    }
}
