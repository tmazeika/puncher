package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import me.mazeika.transhift.puncher.server.Server;
import me.mazeika.transhift.puncher.server.ServerImpl;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Server.class).to(ServerImpl.class);
    }
}
