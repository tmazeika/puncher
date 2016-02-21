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

public class TagFilter extends BaseFilter
{
    private static final Logger logger = LogManager.getLogger();

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException
    {
        final Buffer message = ctx.getMessage();
        final RemoteType remoteType = Attributes.REMOTE_TYPE.get(ctx);

        if (message.position() < 6) {
            logger.debug("{}: waiting on {} more byte(s)...", ctx.getAddress(),
                    6 - message.remaining());

            return ctx.getStopAction(message);
        }

        logger.debug("{}: TagFilter#handleRead(FilterChainContext) -> {}",
                ctx.getAddress(), remoteType.name());

        return ctx.getStopAction();
    }
}
