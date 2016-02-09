package me.mazeika.transhift.puncher.server.handlers.dialable;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import me.mazeika.transhift.puncher.server.IdMapper;

@Singleton @ChannelHandler.Sharable
public class IdDialableHandler extends ChannelHandlerAdapter
{
    private final IdMapper idMapper;

    @Inject
    public IdDialableHandler(IdMapper idMapper)
    {
        this.idMapper = idMapper;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        final byte[] id = idMapper.generateFor(ctx);

        // write out new ID
        ctx.writeAndFlush(ctx.alloc().buffer(id.length).writeBytes(id));

        super.handlerAdded(ctx);
    }
}
