package com.crpc.core.proxy.jdk;



import com.crpc.core.client.RpcReferenceWrapper;
import com.crpc.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;


/**
 * jdkproxy工厂
 *
 * @author liuhuaicong
 * @date 2023/08/09
 */
public class JDKProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) {
        return (T) Proxy.newProxyInstance(rpcReferenceWrapper.getAimClass().getClassLoader(), new Class[]{rpcReferenceWrapper.getAimClass()},
                new JDKClientInvocationHandler(rpcReferenceWrapper));
    }

}
