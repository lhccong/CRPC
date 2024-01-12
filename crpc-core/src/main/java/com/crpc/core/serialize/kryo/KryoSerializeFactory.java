package com.crpc.core.serialize.kryo;

import com.crpc.core.serialize.SerializeFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;



/**
 * 克里奥序列化工厂
 *
 * @author liuhuaicong
 * @date 2023/10/18
 */
@Slf4j
public class KryoSerializeFactory implements SerializeFactory {

    private static final ThreadLocal<Kryo> KRYOS = ThreadLocal.withInitial(Kryo::new);

    @Override
    public <T> byte[] serialize(T t) {
        Output output = null;
        try {
            Kryo kryo = KRYOS.get();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            output = new Output(byteArrayOutputStream);
            kryo.writeClassAndObject(output, t);
            return output.toBytes();
        } catch (Exception e) {
            log.error("序列化失败{}",e.getMessage());
            return new byte[0];
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Input input = null;
        try {
            Kryo kryo = KRYOS.get();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            input = new Input(byteArrayInputStream);
            return (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            log.error("反序列化失败{}",e.getMessage());
            return null;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

}
