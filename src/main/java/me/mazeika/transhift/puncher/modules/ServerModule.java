package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import me.mazeika.transhift.puncher.server.*;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Acceptor.class).to(AcceptorImpl.class);
        bind(Processor.class).to(ProcessorImpl.class);
        bind(Server.class).to(ServerImpl.class);
    }
}
