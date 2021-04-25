package com.example.simple_rpc.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.List;

public class FrameDecoder extends LengthFieldBasedFrameDecoder {
    public FrameDecoder() {
        super(Integer.MAX_VALUE, 0, 4, 0, 4);
    }

    @Override
    protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int i = in.readableBytes();
        if (i >= 8) {
            in.markReaderIndex();
            in.readInt();
            int magic = in.readInt();
            in.resetReaderIndex();
            if (magic != 12345678) {
                return;
            }
        }

        super.callDecode(ctx, in, out);
    }
}
