package com.crpc.core.common.event.listener;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.event.CRpcNodeChangeEvent;
import com.crpc.core.registry.URL;
import com.crpc.core.registry.zookeeper.ProviderNodeInfo;

import java.util.List;

import static com.crpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static com.crpc.core.common.cache.CommonClientCache.CROUTER;

/**
 * 提供者节点数据更改侦听器
 *
 * @author liuhuaicong
 * @date 2023/08/28
 */
public class ProviderNodeDataChangeListener implements CRpcListener<CRpcNodeChangeEvent> {
    @Override
    public void callBack(Object t) {
        ProviderNodeInfo providerNodeInfo = (ProviderNodeInfo) t;
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerNodeInfo.getServiceName());
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            //重置分组信息
            String address = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            if (address.equals(providerNodeInfo.getAddress())) {
                channelFutureWrapper.setGroup(providerNodeInfo.getGroup());
                //修改权重
                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
                URL url = new URL();
                url.setServiceName(providerNodeInfo.getServiceName());
                //更新权重
                CROUTER.updateWeight(url);
                break;
            }
        }
    }
}
