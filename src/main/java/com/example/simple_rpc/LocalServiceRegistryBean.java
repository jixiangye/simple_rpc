package com.example.simple_rpc;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LocalServiceRegistryBean {
    private static Map<String, Map<URL, Channel>> serviceMap = new ConcurrentHashMap<>();

    public static Channel getChannel(String serviceName, URL url) {
        return serviceMap.get(serviceName).get(url);
    }

    public static List<URL> get(String serviceName) {
        Map<URL, Channel> urlChannelMap = serviceMap.get(serviceName);
        return urlChannelMap == null ? new ArrayList<>() : new ArrayList<>(urlChannelMap.keySet());
    }

    public static void add(String serviceName, URL url, Channel channel) {
        Map<URL, Channel> urlChannelMap = serviceMap.get(serviceName);

        if (urlChannelMap == null) {
            urlChannelMap = new ConcurrentHashMap<>();
        }
        urlChannelMap.put(url, channel);
        serviceMap.put(serviceName, urlChannelMap);
    }

    public static void remove(String serviceName, URL deleteUrl) {
        Map<URL, Channel> urlChannelMap = serviceMap.get(serviceName);
        if (urlChannelMap != null) {
            urlChannelMap.remove(deleteUrl);
        }
    }
}
