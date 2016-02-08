package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.netty.channel.ChannelHandler;
import me.mazeika.transhift.puncher.server.*;
import me.mazeika.transhift.puncher.server.handlers.ClientCreationHandler;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Server.class).to(NettyServer.class);
        bind(IdPool.class).to(ClientIdPool.class);
    }

    @Provides
    protected ChannelHandler[] provideChannelHandlers(ClientCreationHandler h0)
    {
        return new ChannelHandler[] { h0 };
    }
}
