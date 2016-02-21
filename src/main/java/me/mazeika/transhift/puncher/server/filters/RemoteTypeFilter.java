package me.mazeika.transhift.puncher.server.filters;

import me.mazeika.transhift.puncher.server.Attributes;
import me.mazeika.transhift.puncher.server.RemoteType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;
import java.util.Optional;

public class RemoteTypeFilter extends BaseFilter
{
    private static final Logger logger = LogManager.getLogger();

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException
    {
        final Buffer message = ctx.getMessage();
        final byte b = message.get(0);
        final Optional<RemoteType> remoteTypeOp = RemoteType.fromByte(b);

        if (! remoteTypeOp.isPresent()) {
            logger.debug("{}: unknown RemoteType 0x{}", ctx.getAddress(),
                    Integer.toHexString(b));
            return ctx.getStopAction();
        }

        switch (remoteTypeOp.get()) {
            case SOURCE:
                logger.debug("{}: got RemoteType.SOURCE", ctx.getAddress());
                Attributes.REMOTE_TYPE.set(ctx, RemoteType.SOURCE);
                break;
            case TARGET:
                logger.debug("{}: got RemoteType.TARGET", ctx.getAddress());
                Attributes.REMOTE_TYPE.set(ctx, RemoteType.TARGET);
                break;
        }

        return ctx.getInvokeAction();
    }
}
