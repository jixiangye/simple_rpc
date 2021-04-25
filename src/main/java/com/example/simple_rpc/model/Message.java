package com.example.simple_rpc.model;

import lombok.Data;

@Data
public class Message<T> {
    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private T body;
}
