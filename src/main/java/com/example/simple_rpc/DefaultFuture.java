package com.example.simple_rpc;


import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.model.Response;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultFuture {
    private static Map<Long, CompletableFuture<Message<Response>>> futureMap = new ConcurrentHashMap<>();

    public static CompletableFuture<Message<Response>> get(Long messageId) {
        return futureMap.get(messageId);
    }

    public static void put(Long messageId, CompletableFuture<Message<Response>> response) {
        futureMap.put(messageId, response);
    }

    public static CompletableFuture<Message<Response>> remove(Long messageId) {
        return futureMap.remove(messageId);
    }
}
