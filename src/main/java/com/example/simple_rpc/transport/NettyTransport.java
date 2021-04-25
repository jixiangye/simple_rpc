package com.example.simple_rpc.transport;

import com.example.simple_rpc.DefaultFuture;
import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.model.Request;
import com.example.simple_rpc.model.Response;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;


public class NettyTransport implements Transport {
    private Channel channel;

    public NettyTransport(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Message<Response> send(Message<Request> request) throws Exception {
        CompletableFuture<Message<Response>> future = new CompletableFuture<>();

        try {
            DefaultFuture.put(request.getHeader().getMessageId(), future);
            channel.writeAndFlush(request).addListener(channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    future.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Exception e) {
            DefaultFuture.remove(request.getHeader().getMessageId());
            future.completeExceptionally(e);
        }

        return future.get();
    }

}
