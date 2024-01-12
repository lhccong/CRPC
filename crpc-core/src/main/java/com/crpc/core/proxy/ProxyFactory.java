package com.crpc.core.proxy;


import com.crpc.core.client.RpcReferenceWrapper;

/**
 * 代理工厂
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public interface ProxyFactory {

    <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable;
}