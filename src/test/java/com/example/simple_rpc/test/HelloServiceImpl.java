package com.example.simple_rpc.test;

public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String name) {
        return "hi " + name;
    }
}
