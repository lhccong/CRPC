package com.crpc.core.router.impl;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.registry.URL;
import com.crpc.core.router.CRouter;
import com.crpc.core.router.Selector;

import java.util.List;

import static com.crpc.core.common.cache.CommonClientCache.*;
import static com.crpc.core.router.impl.RandomRouterImpl.createRandomArr;
import static com.crpc.core.router.impl.RandomRouterImpl.createWeightArr;

/**
 * 轮询策略
 *
 * @author liuhuaicong
 * @date 2023/08/28
 */
public class RotateRouterImpl implements CRouter {
    @Override
    public void refreshRouteArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];
        for (int i = 0; i < channelFutureWrappers.size(); i++) {
            arr[i] = channelFutureWrappers.get(i);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(),arr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getChannelFutureWrappers());
    }

    @Override
    public void updateWeight(URL url) {
        //轮询无权重
    }

}
