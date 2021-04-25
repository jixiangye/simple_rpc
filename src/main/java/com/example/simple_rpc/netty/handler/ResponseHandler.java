package com.example.simple_rpc.netty.handler;

import com.example.simple_rpc.DefaultFuture;
import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.model.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;

public class ResponseHandler extends SimpleChannelInboundHandler<Message<Response>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<Response> msg) throws Exception {
        CompletableFuture<Message<Response>> future = DefaultFuture.get(msg.getHeader().getMessageId());
        if (future != null) {
            future.complete(msg);
        }
    }
}
