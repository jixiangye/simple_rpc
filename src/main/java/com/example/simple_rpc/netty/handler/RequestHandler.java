package com.example.simple_rpc.netty.handler;

import com.example.simple_rpc.model.Header;
import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.model.Request;
import com.example.simple_rpc.model.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class RequestHandler extends SimpleChannelInboundHandler<Message<Request>> {

    private Object ref;

    public RequestHandler(Object ref) {
        this.ref = ref;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<Request> msg) throws Exception {
        long messageId = msg.getHeader().getMessageId();
        Request request = msg.getBody();

        Class<?> aClass = Class.forName(request.getServiceName());
        Method method = aClass.getMethod(request.getMethodName(), request.getArgTypes());

        Message<Response> responseMessage = new Message<>();
        Header header = new Header();
        header.setMagic(12345678);
        header.setIsReq(1);
        header.setMessageId(messageId);
        responseMessage.setHeader(header);

        try {
            Object result = method.invoke(ref, request.getArgs());

            Response response = new Response();
            response.setCode(0);
            response.setErrMsg(null);
            response.setResult(result);
            responseMessage.setBody(response);
        } catch (Exception e) {
            Response response = new Response();
            response.setCode(-1);
            response.setErrMsg(e.getMessage());
            response.setResult(null);
            responseMessage.setBody(response);
        }

        ctx.writeAndFlush(responseMessage);
    }
}
