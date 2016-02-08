package me.mazeika.transhift.puncher.server;

import com.google.inject.Inject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.mazeika.transhift.puncher.cli.CliModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server implements IServer
{
    private static final Logger logger = LogManager.getLogger();

    private final CliModel cli;

    @Inject
    public Server(CliModel cli)
    {
        this.cli = cli;
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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch)
                        {
                            ch.pipeline().addLast();
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            final ChannelFuture f = b.bind(cli.getHost(), cli.getPort()).sync();

            f.channel().closeFuture().sync();
        }
        finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
