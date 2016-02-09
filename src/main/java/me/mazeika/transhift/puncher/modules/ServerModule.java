package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.netty.channel.ChannelHandler;
import me.mazeika.transhift.puncher.server.*;
import me.mazeika.transhift.puncher.server.handlers.InitHandler;
import me.mazeika.transhift.puncher.server.handlers.dialable.IdDialableHandler;
import me.mazeika.transhift.puncher.server.handlers.dialer.IdDialerHandler;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IdMapper.class).to(IdMapperImpl.class);
        bind(Server.class).to(NettyServer.class);

        bind(ChannelHandler.class).annotatedWith(Names.named("init"))
                .to(InitHandler.class);
        bind(ChannelHandler.class).annotatedWith(Names.named("dialable-h"))
                .to(IdDialableHandler.class);
        bind(ChannelHandler.class).annotatedWith(Names.named("dialer-h"))
                .to(IdDialerHandler.class);
    }
}
