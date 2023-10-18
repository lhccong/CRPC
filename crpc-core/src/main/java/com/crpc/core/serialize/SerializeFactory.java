package com.crpc.core.serialize;


/**
 * 序列化工厂
 *
 * @author liuhuaicong
 * @date 2023/10/18
 */
public interface SerializeFactory {


    /**
     * 序列 化
     * 序列化
     *
     * @param t t
     * @return {@link byte[]}
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     *
     * @param data  数据
     * @param clazz 克拉兹
     * @return {@link T}
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
