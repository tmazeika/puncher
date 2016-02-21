package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import me.mazeika.transhift.puncher.server.Server;
import me.mazeika.transhift.puncher.server.ServerImpl;
import me.mazeika.transhift.puncher.server.filters.RemoteTypeFilter;
import me.mazeika.transhift.puncher.server.filters.TagFilter;
import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.ssl.SSLFilter;

public class ServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Server.class).to(ServerImpl.class);
    }

    @Provides
    Filter[] provideFilters(
            TransportFilter f0, SSLFilter f1, RemoteTypeFilter f2, TagFilter f3)
    {
        return new Filter[] { f0, /*f1,*/ f2, f3, };
    }
}
