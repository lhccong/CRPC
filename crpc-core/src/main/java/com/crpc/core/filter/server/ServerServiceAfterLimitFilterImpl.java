package com.crpc.core.filter.server;

import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.ServerServiceSemaphoreWrapper;
import com.crpc.core.common.annotations.SPI;
import com.crpc.core.filter.ServerFilter;
import static com.crpc.core.common.cache.CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP;


/**
 * 限制过滤器后服务器服务实现
 *
 * @author cong
 * @date 2024/01/31
 */
@SPI("after")
public class ServerServiceAfterLimitFilterImpl implements ServerFilter {

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        serverServiceSemaphoreWrapper.getSemaphore().release();
    }
}
