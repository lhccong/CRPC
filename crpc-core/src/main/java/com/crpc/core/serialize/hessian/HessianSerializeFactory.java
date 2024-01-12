package com.crpc.core.serialize.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.crpc.core.serialize.SerializeFactory;
import lombok.extern.slf4j.Slf4j;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * 黑森序列化工厂
 *
 * @author liuhuaicong
 * @date 2023/10/18
 */
@Slf4j
public class HessianSerializeFactory implements SerializeFactory {

    @Override
    public <T> byte[] serialize(T t) {
        byte[] data;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output(os);
            output.writeObject(t);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            data = os.toByteArray();
        } catch (Exception e) {
           log.error("序列化失败",e);
           return new byte[0];
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null) {
            return null;
        }
        Object result;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            Hessian2Input input = new Hessian2Input(is);
            result = input.readObject();
        } catch (Exception e) {
            log.error("反序列化失败{}",e.getMessage());
            return null;
        }
        return (T) result;
    }

}
