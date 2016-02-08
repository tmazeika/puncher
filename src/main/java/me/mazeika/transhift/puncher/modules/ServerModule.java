package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import me.mazeika.transhift.puncher.server.*;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IServer.class).to(Server.class);
    }
}
