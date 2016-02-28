package me.mazeika.transhift.puncher.server;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Acceptor.class).to(AcceptorImpl.class);
        bind(Processor.class).to(ProcessorImpl.class);
        bind(Server.class).to(ServerImpl.class);

        // bind global Remote pool
        bind(new TypeLiteral<Collection<Remote>>(){})
                .annotatedWith(Remote.Pool.class)
                .toInstance(ConcurrentHashMap.newKeySet());

        // install Remote.Factory
        install(new FactoryModuleBuilder()
                .implement(Remote.class, RemoteImpl.class)
                .build(Remote.Factory.class));
    }
}
