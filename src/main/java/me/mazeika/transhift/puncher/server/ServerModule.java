package me.mazeika.transhift.puncher.server;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.mazeika.transhift.puncher.server.*;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Acceptor.class).to(AcceptorImpl.class);
        bind(Processor.class).to(ProcessorImpl.class);
        bind(Server.class).to(ServerImpl.class);
        bind(TagPool.class).to(TagPoolImpl.class);

        // install Remote.Factory
        install(new FactoryModuleBuilder()
                .implement(Remote.class, RemoteImpl.class)
                .build(Remote.Factory.class));

        // install Tag.Factory
        install(new FactoryModuleBuilder()
                .implement(Tag.class, TagImpl.class)
                .build(Tag.Factory.class));
    }
}
