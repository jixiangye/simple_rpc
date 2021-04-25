package com.example.simple_rpc;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.example.simple_rpc.model.Header;
import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.model.Request;
import com.example.simple_rpc.model.Response;
import com.example.simple_rpc.transport.NettyTransport;
import com.example.simple_rpc.transport.Transport;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RpcInvocationHandler implements InvocationHandler {
    private String serviceName;

    private static AtomicLong atomicLong = new AtomicLong();

    public RpcInvocationHandler(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<URL> urls = LocalServiceRegistryBean.get(serviceName);
        if (CollectionUtils.isEmpty(urls)) {
            throw new RuntimeException("不存在可用服务");
        }
        URL url = urls.get(ThreadLocalRandom.current().nextInt(urls.size()));
        Channel channel = LocalServiceRegistryBean.getChannel(serviceName, url);

        if (!channel.isActive()) {
            log.error("当前channel不可用");
            throw new RuntimeException("当前channel不可用");
        }

        Transport transport = new NettyTransport(channel);
        Message<Request> requestMessage = new Message<>();
        Header header = new Header();
        header.setMagic(12345678);
        header.setMessageId(atomicLong.getAndIncrement());
        requestMessage.setHeader(header);
        Request request = new Request();
        request.setServiceName(method.getDeclaringClass().getCanonicalName());
        request.setMethodName(method.getName());
        request.setArgTypes(method.getParameterTypes());
        request.setArgs(args);

        requestMessage.setBody(request);

        Message<Response> responseMessage = transport.send(requestMessage);
        return responseMessage.getBody().getResult();
    }
}
