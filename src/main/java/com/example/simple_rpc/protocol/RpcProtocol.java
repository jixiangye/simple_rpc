package com.example.simple_rpc.protocol;

import com.example.simple_rpc.LocalServiceRegistryBean;
import com.example.simple_rpc.RpcInvocationHandler;
import com.example.simple_rpc.URL;
import com.example.simple_rpc.netty.codec.Decoder;
import com.example.simple_rpc.netty.codec.Encoder;
import com.example.simple_rpc.netty.codec.FrameDecoder;
import com.example.simple_rpc.netty.codec.FrameEncoder;
import com.example.simple_rpc.netty.handler.RequestHandler;
import com.example.simple_rpc.netty.handler.ResponseHandler;
import com.example.simple_rpc.registry.NacosRegistry;
import com.example.simple_rpc.registry.Registry;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.*;
import java.util.List;

@Slf4j
public class RpcProtocol<T> implements Protocol<T> {
    private static int DEFAULT_PORT = 20880;

    private Registry registry = new NacosRegistry();

    @Override
    public void export(T ref, Class interfaceClass) throws Exception {
        //创建服务端监听
        createServer(ref);

        //注册服务到注册中心
        register(interfaceClass);
    }

    private void register(Class interfaceClass) throws Exception {
        registry.register(interfaceClass.getCanonicalName(), new URL(getLocalHost(), DEFAULT_PORT));
    }

    @Override
    public T refer(Class interfaceClass) throws Exception {
        //创建客户端连接
        initClient(interfaceClass.getCanonicalName());

        //创建代理
        T proxyInstance = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{interfaceClass}, new RpcInvocationHandler(interfaceClass.getCanonicalName()));
        return proxyInstance;
    }

    private void initClient(String serviceName) throws Exception {

        Bootstrap bootstrap = newNettyClient();

        List<URL> lookup = registry.lookup(serviceName);
        for (URL url : lookup) {
            ChannelFuture channelFuture = bootstrap.connect(url.getHost(), url.getPort()).sync();
            Channel channel = channelFuture.channel();
            LocalServiceRegistryBean.add(serviceName, url, channel);
        }

        registry.subscribe(serviceName);

    }

    public static Bootstrap newNettyClient() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline//.addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new FrameDecoder())
                                .addLast(new Decoder())
                                .addLast(new FrameEncoder())
                                .addLast(new Encoder())
                                .addLast(new ResponseHandler());
                    }
                });
        return bootstrap;
    }

    private void createServer(T ref) throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //.handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline//.addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new FrameDecoder())
                                .addLast(new Decoder())
                                .addLast(new FrameEncoder())
                                .addLast(new Encoder())
                                .addLast(new RequestHandler(ref));
                    }
                });

        serverBootstrap.bind(DEFAULT_PORT).sync();
    }

    private static String getLocalHost() {
        String hostToBind = null;
        try {
            hostToBind = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if ("127.0.0.1".equals(hostToBind) || "localhost".equals(hostToBind)) {
            try (Socket socket = new Socket()) {
                URL registryURL = getRegistryURL();
                SocketAddress addr = new InetSocketAddress(registryURL.getHost(), registryURL.getPort());
                socket.connect(addr, 1000);
                hostToBind = socket.getLocalAddress().getHostAddress();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        return hostToBind;
    }

    private static URL getRegistryURL() {
        String serveAddr = System.getProperty("serveAddr");
        String[] split = serveAddr.split(":");
        String host = split[0];
        int port = Integer.valueOf(split[1]);
        return new URL(host, port);
    }

}
