package me.mazeika.transhift.puncher.server.handlers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.mazeika.transhift.puncher.server.Client;

@Singleton @ChannelHandler.Sharable
public class ClientCreationHandler extends SimpleChannelInboundHandler
{
    private final Provider<Client> clientProvider;

    @Inject
    public ClientCreationHandler(Provider<Client> clientProvider)
    {
        this.clientProvider = clientProvider;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg)
    {
        ctx.attr(Client.ATTR).set(clientProvider.get());
    }
}
