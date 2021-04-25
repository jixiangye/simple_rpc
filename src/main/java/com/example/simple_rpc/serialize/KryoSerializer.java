package com.example.simple_rpc.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) throws Exception {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Output output = new Output(bos);
        kryo.writeObject(output, obj);
        output.flush();
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws Exception {
        Kryo kryo = new Kryo();
        Input input = new Input(new ByteArrayInputStream(data));
        return kryo.readObject(input, clz);
    }

}
