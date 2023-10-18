package com.crpc.core.serialize.fastjson;

import com.alibaba.fastjson.JSON;
import com.crpc.core.serialize.SerializeFactory;


/**
 * 快速 JSON 序列化工厂
 *
 * @author liuhuaicong
 * @date 2023/10/18
 */
public class FastJsonSerializeFactory implements SerializeFactory {

    @Override
    public <T> byte[] serialize(T t) {
        String jsonStr = JSON.toJSONString(t);
        return jsonStr.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(new String(data),clazz);
    }

}
