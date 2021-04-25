package com.example.simple_rpc.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    /**
     * 响应的错误码，正常响应为0，非0表示异常响应
     */
    private int code = 0;

    /**
     * 异常信息
     */
    private String errMsg;

    /**
     * 响应结果
     */
    private Object result;
}
