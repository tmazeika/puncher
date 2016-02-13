package me.mazeika.transhift.puncher.modules;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import io.netty.channel.ChannelHandler;
import me.mazeika.transhift.puncher.server.*;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Service.class)
                .annotatedWith(Server.Bind.class)
                .to(Server.class);

        bind(Service.class)
                .annotatedWith(Acceptor.Bind.class)
                .to(Acceptor.class);
    }
}
