package me.mazeika.transhift.puncher.modules;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import me.mazeika.transhift.puncher.server.Acceptor;
import me.mazeika.transhift.puncher.server.Server;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Service.class)
                .annotatedWith(Acceptor.Bind.class)
                .to(Acceptor.class);

        bind(Service.class)
                .annotatedWith(Server.Bind.class)
                .to(Server.class);
    }
}
