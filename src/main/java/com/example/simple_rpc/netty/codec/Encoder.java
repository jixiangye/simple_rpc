package com.example.simple_rpc.netty.codec;

import com.example.simple_rpc.model.Header;
import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.serialize.KryoSerializer;
import com.example.simple_rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class Encoder extends MessageToMessageEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        Header header = msg.getHeader();
        Object body = msg.getBody();
        buffer.writeInt(header.getMagic());
        buffer.writeInt(header.getIsReq());
        buffer.writeLong(header.getMessageId());

        Serializer serializer = new KryoSerializer();
        buffer.writeBytes(serializer.serialize(body));
        out.add(buffer);
    }
}
