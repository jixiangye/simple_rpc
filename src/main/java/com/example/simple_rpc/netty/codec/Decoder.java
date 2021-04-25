package com.example.simple_rpc.netty.codec;

import com.example.simple_rpc.model.Header;
import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.model.Request;
import com.example.simple_rpc.model.Response;
import com.example.simple_rpc.serialize.KryoSerializer;
import com.example.simple_rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class Decoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int magic = msg.readInt();
        int isReq = msg.readInt();
        long messageId = msg.readLong();
        byte[] payload = new byte[msg.readableBytes()];
        msg.readBytes(payload);
        Header header = new Header();
        header.setMagic(magic);
        header.setMessageId(messageId);

        Object body = null;
        Serializer serializer = new KryoSerializer();
        if (isReq == 0) {
            body = serializer.deserialize(payload, Request.class);
        } else {
            body = serializer.deserialize(payload, Response.class);
        }

        Message message = new Message<>();
        message.setHeader(header);
        message.setBody(body);
        out.add(message);
    }
}
