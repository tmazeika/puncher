package me.mazeika.transhift.puncher.server.handlers.dialable;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import me.mazeika.transhift.puncher.server.IdPool;

@Singleton @ChannelHandler.Sharable
public class IdDialableHandler extends ChannelHandlerAdapter
{
    private final IdPool idPool;

    @Inject
    public IdDialableHandler(IdPool idPool)
    {
        this.idPool = idPool;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        final byte[] id = idPool.generateFor(ctx);

        // write out new ID
        ctx.writeAndFlush(ctx.alloc().buffer(id.length).writeBytes(id));

        super.handlerAdded(ctx);
    }
}
