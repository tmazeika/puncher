package me.mazeika.transhift.puncher.server.filters;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

public class EchoFilter extends BaseFilter
{
    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException
    {
        final Object peerAddress = ctx.getAddress();
        final Buffer message = ctx.getMessage();

        System.out.println(message);

        ctx.write(message, null);
        return ctx.getStopAction();
    }


}
