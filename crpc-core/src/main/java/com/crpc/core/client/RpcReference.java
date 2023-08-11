package com.crpc.core.client;


import com.crpc.core.proxy.ProxyFactory;

/**
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public class RpcReference {

    public ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }


    /**
     * 根据接口类型获取代理对象
     *
     * @param tClass t类
     * @return {@link T}
     * @throws Throwable throwable
     */
    public <T> T get(Class<T> tClass) throws Throwable {
        return proxyFactory.getProxy(tClass);
    }
}
