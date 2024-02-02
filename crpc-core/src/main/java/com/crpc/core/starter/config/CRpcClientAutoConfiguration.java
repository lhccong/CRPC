package com.crpc.core.starter.config;


import com.crpc.core.client.Client;
import com.crpc.core.client.ConnectionHandler;
import com.crpc.core.client.RpcReference;
import com.crpc.core.client.RpcReferenceWrapper;
import com.crpc.core.starter.common.CRpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;

/**
 * CRPC客户端自动配置
 *
 * @author cong
 * @date 2024/02/01
 */
@Slf4j
public class CRpcClientAutoConfiguration implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {

    private static RpcReference rpcReference = null;
    private static Client client = null;
    private volatile boolean needInitClient = false;
    private volatile boolean hasInitClientConfig = false;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(CRpcReference.class)) {
                if (!hasInitClientConfig) {
                    client = new Client();
                    try {
                        rpcReference = client.initClientApplication();
                    } catch (Exception e) {
                        log.error("[CRpcClientAutoConfiguration] postProcessAfterInitialization has error ",e);
                        throw new RuntimeException(e);
                    }
                    hasInitClientConfig = true;
                }
                needInitClient = true;
                CRpcReference cRpcListenerLoader = field.getAnnotation(CRpcReference.class);
                try {
                    field.setAccessible(true);
                    Object refObj;
                    RpcReferenceWrapper rpcReferenceWrapper = new RpcReferenceWrapper();
                    rpcReferenceWrapper.setAimClass(field.getType());
                    rpcReferenceWrapper.setGroup(cRpcListenerLoader.group());
                    rpcReferenceWrapper.setServiceToken(cRpcListenerLoader.serviceToken());
                    rpcReferenceWrapper.setUrl(cRpcListenerLoader.url());
                    rpcReferenceWrapper.setTimeOut(cRpcListenerLoader.timeOut());
                    //失败重试次数
                    rpcReferenceWrapper.setRetry(cRpcListenerLoader.retry());
                    rpcReferenceWrapper.setAsync(cRpcListenerLoader.async());
                    refObj = rpcReference.get(rpcReferenceWrapper);
                    field.set(bean, refObj);
                    client.doSubscribeService(field.getType());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        return bean;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (needInitClient && client!=null) {
            log.info(" ================== [{}] started success ================== ",client.getClientConfig().getApplicationName());
            ConnectionHandler.setBootstrap(client.getBootstrap());
            client.doConnectServer();
            client.startClient();
        }
    }
}
