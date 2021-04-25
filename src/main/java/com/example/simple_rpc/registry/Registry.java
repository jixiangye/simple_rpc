package com.example.simple_rpc.registry;


import com.example.simple_rpc.URL;

import java.util.List;

public interface Registry {
    void register(String serviceName, URL url) throws Exception;

    void subscribe(String serviceName) throws Exception;

    List<URL> lookup(String serviceName) throws Exception;
}
