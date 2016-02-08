package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.netty.channel.ChannelHandler;
import me.mazeika.transhift.puncher.server.*;
import me.mazeika.transhift.puncher.server.handlers.TypeHandler;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IServer.class).to(Server.class);
    }

    @Provides
    protected ChannelHandler[] provideChannelHandlers(TypeHandler h0)
    {
        return new ChannelHandler[] { h0 };
    }
}
