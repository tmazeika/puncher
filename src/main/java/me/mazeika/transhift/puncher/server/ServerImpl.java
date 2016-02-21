package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.binding_annotations.Args;
import me.mazeika.transhift.puncher.server.filters.EchoFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOServerConnection;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.ssl.SSLFilter;

import javax.inject.Inject;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class ServerImpl implements Server
{
    private static final Logger logger = LogManager.getLogger();

    private final Object lock = new Object();
    private final String host;
    private final int port;

    private boolean shuttingDown;

    @Inject
    public ServerImpl(@Args.Host String host, @Args.Port int port)
    {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start()
    {
        final FilterChainBuilder builder =
                FilterChainBuilder.stateless();

        builder.add(new TransportFilter())
               .add(new SSLFilter())
               .add(new EchoFilter());

        final TCPNIOTransport transport =
                TCPNIOTransportBuilder.newInstance().build();

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
