package com.example.simple_rpc.serialize;

public interface Serializer {
    /**
     * 序列化
     *
     * @param obj 对象
     * @return 字节数组
     * @throws Exception
     */
    byte[] serialize(Object obj) throws Exception;

    /**
     * 反序列化
     *
     * @param data 字节数组
     * @param clz  类
     * @param <T>  泛型
     * @return 对象
     * @throws Exception
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws Exception;
}
