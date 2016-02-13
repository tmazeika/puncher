package me.mazeika.transhift.puncher.server.handlers.dialer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class IdDialerHandler extends ReplayingDecoder<Void>
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception
    {
        // read in ID
//        ctx.attr(Attributes.ID).set(in.readBytes(IdMapper.ID_LENGTH).array());
    }
}
