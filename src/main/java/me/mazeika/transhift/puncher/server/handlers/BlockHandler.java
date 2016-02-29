package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.SourceConnectNotifier;
import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;

class BlockHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    private final Object monitor = new Object();
    private final Provider<Handler> addressExchangeHandlerProvider;
    private final SourceConnectNotifier notifier;

    private volatile Remote peer;

    @Inject
    public BlockHandler(@AddressExchange final Provider<Handler>
                                    addressExchangeHandlerProvider,
                        final SourceConnectNotifier notifier)
    {
        this.addressExchangeHandlerProvider = addressExchangeHandlerProvider;
        this.notifier = notifier;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        notifier.register(remote, (source, tag) -> {
            if (peer == null &&
                    remote.meta().get(MetaKeys.TAG).get().equals(tag)) {
                peer = source;

                monitor.notify();

                return true;
            }

            return false;
        });

        // wait for a peer to dial this remote
        synchronized (monitor) {
            while (peer == null) {
                logger.trace("{}: beginning block", remote);
                monitor.wait();
                logger.trace("{}: block awake", remote);
            }
        }

        remote.meta().set(MetaKeys.PEER, peer);
        addressExchangeHandlerProvider.get().handle(remote);
    }
}
