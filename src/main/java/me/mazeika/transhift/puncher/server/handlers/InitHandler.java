package me.mazeika.transhift.puncher.server.handlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Singleton @ChannelHandler.Sharable
public class InitHandler extends ReplayingDecoder<Void>
{
    private static final byte DIALABLE_BYTE = 0x00;
    private static final byte DIALER_BYTE   = 0x01;

    private static final Logger logger = LogManager.getLogger();

    private final ChannelHandler dialableHandler;
    private final ChannelHandler dialerHandler;

    @Inject
    public InitHandler(@Named("dialable-h") ChannelHandler dialableHandler,
                       @Named("dialer-h") ChannelHandler dialerHandler)
    {
        this.dialableHandler = dialableHandler;
        this.dialerHandler = dialerHandler;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception
    {
        // read in dial
        final byte b = in.readByte();

        switch (b) {
            case DIALABLE_BYTE:
                ctx.pipeline().addLast(dialableHandler);
                out.add(new Object());
                break;
            case DIALER_BYTE:
                ctx.pipeline().addLast(dialerHandler);
                out.add(new Object());
                break;
            default:
                logger.warn("Got invalid dial byte ({}) from {}", b,
                        ctx.channel().remoteAddress());
                ctx.close();
        }

        ctx.pipeline().remove(this);
    }
}
