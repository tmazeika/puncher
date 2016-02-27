package me.mazeika.transhift.puncher.server.handlers;

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

    private final Provider<TagConsumptionHandler> tagConsumptionHandlerProvider;
    private final Provider<TagProductionHandler> tagProductionHandlerProvider;

    @Inject
    public TypeHandler(
            @Handler.TagConsumption final Provider<TagConsumptionHandler>
                    tagConsumptionHandlerProvider,
            @Handler.TagProduction final Provider<TagProductionHandler>
                    tagProductionHandlerProvider)
    {
        this.tagConsumptionHandlerProvider = tagConsumptionHandlerProvider;
        this.tagProductionHandlerProvider = tagProductionHandlerProvider;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        final byte b = remote.waitAndRead();
        final Optional<RemoteType> typeOp = RemoteType.fromByte(b);

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
            logger.debug("{}: got wrong RemoteType: {}", remote,
                    Integer.toHexString(b));
        }
    }
}
