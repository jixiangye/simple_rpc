package com.example.simple_rpc.model;

import lombok.Data;

@Data
public class Header {
    /**
     * 魔数，报文必须以这个数开头才会解析
     */
    private int magic = 12345678;

    /**
     * 请求：0，响应：1
     */
    private int isReq = 0;

    /**
     * 消息id
     */
    private long messageId;
}
