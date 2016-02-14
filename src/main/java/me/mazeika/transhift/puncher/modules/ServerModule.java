package me.mazeika.transhift.puncher.modules;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.mazeika.transhift.puncher.binding_annotations.Args;
import me.mazeika.transhift.puncher.binding_annotations.BindAddress;
import me.mazeika.transhift.puncher.server.Acceptor;
import me.mazeika.transhift.puncher.server.AcceptorFactory;
import me.mazeika.transhift.puncher.server.Server;

import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new FactoryModuleBuilder()
                .implement(Service.class, Acceptor.Bind.class, Acceptor.class)
                .build(AcceptorFactory.class));

        bind(Service.class)
                .annotatedWith(Server.Bind.class)
                .to(Server.class);

        bind(new TypeLiteral<Queue<SocketChannel>>(){})
                .annotatedWith(Server.Sockets.class)
                .toInstance(new LinkedBlockingQueue<>());

        bind(new TypeLiteral<Queue<byte[]>>(){})
                .annotatedWith(Server.Outbound.class)
                .toInstance(new LinkedBlockingQueue<>());
    }

    @Provides @Singleton @BindAddress
    SocketAddress provideBindAddress(@Args.Host String host,
                                     @Args.Port int port)
    {
        return new InetSocketAddress(host, port);
    }
}
