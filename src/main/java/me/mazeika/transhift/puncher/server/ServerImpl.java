package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.binding_annotations.Args;
import me.mazeika.transhift.puncher.server.filters.RemoteTypeFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.NIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.ssl.SSLFilter;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServerImpl implements Server
{
    private static final Logger logger = LogManager.getLogger();

    private final Object lock = new Object();
    private final String host;
    private final int port;
    private final Filter[] filters;

    private boolean shuttingDown;

    @Inject
    public ServerImpl(@Args.Host String host, @Args.Port int port,
                      Filter[] filters)
    {
        this.host = host;
        this.port = port;
        this.filters = filters;
    }

    @Override
    public void start()
    {
        final FilterChainBuilder builder = FilterChainBuilder.stateless();
        final NIOTransport transport =
                TCPNIOTransportBuilder.newInstance().build();

        builder.addAll(filters);
        transport.setProcessor(builder.build());

        // try to shutdown gracefully on JVM termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shuttingDown = true;

            synchronized (lock) {
                lock.notify();
            }

            transport.shutdown(5, TimeUnit.SECONDS);
        }));

        try {
            final Connection conn = transport.bind(host, port);

            transport.start();
            logger.info("Listening at {}", conn.getLocalAddress());
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);

            try {
                transport.shutdownNow();
            }
            catch (IOException e1) {
                logger.error(e1.getMessage(), e1);
            }
        }

        // wait for JVM shutdown
        synchronized (lock) {
            while (! shuttingDown) {
                try {
                    lock.wait();
                }
                catch (InterruptedException ignored) { }
            }
        }
    }
}
