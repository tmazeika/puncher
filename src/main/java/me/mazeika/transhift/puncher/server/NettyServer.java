package me.mazeika.transhift.puncher.server;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.mazeika.transhift.puncher.cli.CliModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyServer implements Server
{
    private static final int BACKLOG = 64;

    private static final Logger logger = LogManager.getLogger();

    private final CliModel cli;
    private final Initializer initializer;

    @Inject
    public NettyServer(CliModel cli, Initializer initializer)
    {
        this.cli = cli;
        this.initializer = initializer;
    }

    @Override
    public void start() throws Exception
    {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            final ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer)
                    .option(ChannelOption.SO_BACKLOG, BACKLOG)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            final Channel ch = b
                    .bind(cli.getHost(), cli.getPort())
                    .sync()
                    .channel();

            logger.info("Listening at {}", ch.localAddress());
            ch.closeFuture().sync();
        }
        finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static class Initializer extends ChannelInitializer<SocketChannel>
    {
        private final Provider<ChannelHandler> initHandlerProvider;

        @Inject
        public Initializer(
                @Named("init-h") Provider<ChannelHandler> initHandlerProvider)
        {
            this.initHandlerProvider = initHandlerProvider;
        }

        @Override
        protected void initChannel(SocketChannel ch)
        {
            ch.pipeline().addLast(initHandlerProvider.get());
        }
    }
}
