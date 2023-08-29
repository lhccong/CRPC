package com.crpc.core.common.event.listener;

import com.crpc.core.common.event.CRpcDestroyEvent;
import com.crpc.core.registry.URL;

import static com.crpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;
import static com.crpc.core.common.cache.CommonServerCache.REGISTRY_SERVICE;

/**
 * 服务注销 监听器
 *
 * @author liuhuaicong
 * @date 2023/08/28
 */
public class ServiceDestroyListener implements CRpcListener<CRpcDestroyEvent>{
    @Override
    public void callBack(Object t) {
        for (URL url : PROVIDER_URL_SET) {
            REGISTRY_SERVICE.unRegister(url);
        }
    }
}
