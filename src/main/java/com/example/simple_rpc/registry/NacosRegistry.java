package com.example.simple_rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.example.simple_rpc.LocalServiceRegistryBean;
import com.example.simple_rpc.protocol.RpcProtocol;
import com.example.simple_rpc.URL;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NacosRegistry implements Registry {
    private NamingService naming;

    public NacosRegistry() {
        try {
            naming = NamingFactory.createNamingService(System.getProperty("serveAddr"));
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String serviceName, URL url) throws Exception {
        naming.registerInstance(serviceName, url.getHost(), url.getPort());
    }

    @Override
    public void subscribe(String serviceName) throws Exception {
        naming.subscribe(serviceName, event -> {
            if (event instanceof NamingEvent) {
                log.info("监听变化");
                NamingEvent namingEvent = (NamingEvent) event;
                List<Instance> instances = namingEvent.getInstances();

                List<URL> newUrls = instances.stream().map(item -> new URL(item.getIp(), item.getPort())).collect(Collectors.toList());
                List<URL> oldUrls = LocalServiceRegistryBean.get(serviceName);
                List<URL> addUrls = newUrls.stream().filter(item -> !oldUrls.contains(item)).collect(Collectors.toList());
                List<URL> deleteUrls = oldUrls.stream().filter(item -> !newUrls.contains(item)).collect(Collectors.toList());
                log.info("上线：{}", addUrls);
                log.info("下线：{}", deleteUrls);


                for (URL deleteUrl : deleteUrls) {
                    LocalServiceRegistryBean.remove(serviceName, deleteUrl);
                }

                for (URL addUrl : addUrls) {
                    Bootstrap bootstrap = RpcProtocol.newNettyClient();
                    ChannelFuture channelFuture = null;
                    try {
                        channelFuture = bootstrap.connect(addUrl.getHost(), addUrl.getPort()).sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Channel channel = channelFuture.channel();

                    LocalServiceRegistryBean.add(serviceName, addUrl, channel);
                }

            }
        });
    }

    @Override
    public List<URL> lookup(String serviceName) throws Exception {
        List<Instance> allInstances = naming.getAllInstances(serviceName);
        List<URL> urls = allInstances.stream().map((Instance instance) ->
                new URL(instance.getIp(), instance.getPort())
        ).collect(Collectors.toList());

        return urls;
    }
}
