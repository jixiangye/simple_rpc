package com.example.simple_rpc.transport;


import com.example.simple_rpc.model.Message;
import com.example.simple_rpc.model.Request;
import com.example.simple_rpc.model.Response;

public interface Transport {
    /**
     * 发送报文
     *
     * @param request 请求
     * @return 响应
     * @throws Exception
     */
    Message<Response> send(Message<Request> request) throws Exception;
}
