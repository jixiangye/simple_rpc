package com.example.simple_rpc.test;


import com.example.simple_rpc.protocol.Protocol;
import com.example.simple_rpc.protocol.RpcProtocol;

public class Server {
    public static void main(String[] args) throws Exception {
        System.setProperty("serveAddr","113.31.114.202:8848");
        HelloService helloService = new HelloServiceImpl();
        Protocol protocol = new RpcProtocol();
        protocol.export(helloService, HelloService.class);


    }
}
