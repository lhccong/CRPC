package com.crpc.core.registry.zookeeper;

import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;

import java.util.List;

import static com.crpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static com.crpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;

/**
 * 抽象注册
 *
 * @author liuhuaicong
 * @date 2023/08/10
 */
public  abstract class AbstractRegister implements RegistryService {
    @Override
    public void register(URL url) {
        PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unRegister(URL url) {
        PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.add(url);
    }

    @Override
    public void doUnSubscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.remove(url.getServiceName());
    }

    /**
     * 留给子类扩展
     *
     * @param url url
     */
    public abstract void doBeforeSubscribe(URL url);
    /**
     * 留给子类扩展
     *
     * @param url url
     */
    public abstract void doAfterSubscribe(URL url);

    /**
     * 留给子类扩展
     *
     * @param serviceName 服务名称
     * @return {@link List}<{@link String}>
     */
    public abstract List<String> getProviderIps(String serviceName);
}
