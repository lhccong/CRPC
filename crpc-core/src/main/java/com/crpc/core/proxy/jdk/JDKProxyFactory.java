package com.crpc.core.proxy.jdk;



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
    public <T> T getProxy(final Class clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new JDKClientInvocationHandler(clazz));
    }

}
