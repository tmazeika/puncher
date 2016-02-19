package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.throwingproviders.CheckedProvider;
import com.google.inject.throwingproviders.CheckedProvides;
import me.mazeika.transhift.puncher.binding_annotations.Args;
import me.mazeika.transhift.puncher.binding_annotations.BindAddress;
import me.mazeika.transhift.puncher.server.TagPool;
import me.mazeika.transhift.puncher.server.TagPoolImpl;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(TagPool.class).to(TagPoolImpl.class);
    }

    @Provides @Singleton @BindAddress
    SocketAddress provideBindAddress(@Args.Host String host,
                                     @Args.Port int port)
    {
        return new InetSocketAddress(host, port);
    }

    @CheckedProvides(SelectorProvider.class) @Singleton
    Selector provideSelector() throws IOException
    {
        return Selector.open();
    }

    interface SelectorProvider<T> extends CheckedProvider<T>
    {
        T get() throws IOException;
    }
}
