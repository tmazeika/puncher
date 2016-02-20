package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.throwingproviders.CheckedProvider;
import com.google.inject.throwingproviders.CheckedProvides;
import me.mazeika.transhift.puncher.binding_annotations.Args;
import me.mazeika.transhift.puncher.binding_annotations.BindAddress;
import me.mazeika.transhift.puncher.TagPool;
import me.mazeika.transhift.puncher.TagPoolImpl;
import me.mazeika.transhift.puncher.server.Server;
import me.mazeika.transhift.puncher.server.ServerImpl;

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
        bind(Server.class).to(ServerImpl.class);
    }
}
