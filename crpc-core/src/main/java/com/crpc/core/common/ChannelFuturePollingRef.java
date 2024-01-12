package com.crpc.core.common;

import java.util.concurrent.atomic.AtomicLong;

import static com.crpc.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

/**
 * 轮询
 *
 * @author liuhuaicong
 * @date 2023/08/22
 */
public class ChannelFuturePollingRef {

    private final AtomicLong referenceTimes = new AtomicLong(0);

    public ChannelFutureWrapper getChannelFutureWrapper(ChannelFutureWrapper[] arr) {
        long i = referenceTimes.getAndIncrement();
        int index = (int) (i % arr.length);
        return arr[index];
    }
}
