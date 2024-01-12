package com.crpc.core.serialize.jdk;


import com.crpc.core.serialize.SerializeFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.*;


/**
 * JDK 序列化工厂
 *
 * @author liuhuaicong
 * @date 2023/10/18
 */
@Slf4j
public class JdkSerializeFactory implements SerializeFactory {


    @Override
    public <T> byte[] serialize(T t) {
        byte[] data;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(os);
            output.writeObject(t);
            output.flush();
            output.close();
            data = os.toByteArray();
        } catch (Exception e) {
            log.error("序列化失败{}",e.getMessage());
            return new byte[0];
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        try {
            ObjectInputStream input = new ObjectInputStream(is);
            Object result = input.readObject();
            return ((T) result);
        } catch (IOException | ClassNotFoundException e) {
            log.error("反序列化失败{}",e.getMessage());
            return null;
        }
    }

}
