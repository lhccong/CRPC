package com.crpc.core.proxy;


/**
 * 代理工厂
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public interface ProxyFactory {

    <T> T getProxy(final Class clazz) throws Throwable;
}