package me.mazeika.transhift.puncher.server.handlers;

import com.google.common.io.ByteStreams;
import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.server.RemoteType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

class TypeHandler implements Handler
{
    private static final Logger logger = LogManager.getLogger();

    private final Provider<Handler> tagConsumptionHandlerProvider;
    private final Provider<Handler> tagProductionHandlerProvider;

    @Inject
    public TypeHandler(
            @Handler.TagConsumption final Provider<Handler>
                    tagConsumptionHandlerProvider,
            @Handler.TagProduction final Provider<Handler>
                    tagProductionHandlerProvider)
    {
        this.tagConsumptionHandlerProvider = tagConsumptionHandlerProvider;
        this.tagProductionHandlerProvider = tagProductionHandlerProvider;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final byte[] b = new byte[1];

        ByteStreams.readFully(remote.in(), b);

        final Optional<RemoteType> typeOp = RemoteType.fromByte(b[0]);

        if (typeOp.isPresent()) {
            switch (typeOp.get()) {
                case SOURCE:
                    tagConsumptionHandlerProvider.get().handle(remote);
                    break;
                case TARGET:
                    tagProductionHandlerProvider.get().handle(remote);
                    break;
            }
        }
        else {
            logger.debug("{}: got wrong RemoteType 0x{}", remote,
                    Integer.toHexString(b[0]));
        }
    }
}
