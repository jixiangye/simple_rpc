package com.example.simple_rpc.protocol;

public interface Protocol<T> {
    /**
     * 暴露服务
     *
     * @param ref 服务实现
     * @throws Exception
     */
    void export(T ref, Class interfaceClass) throws Exception;

    /**
     * 引用服务
     *
     * @param interfaceClass 服务接口class
     * @return 代理类
     * @throws Exception
     */
    T refer(Class interfaceClass) throws Exception;
}
