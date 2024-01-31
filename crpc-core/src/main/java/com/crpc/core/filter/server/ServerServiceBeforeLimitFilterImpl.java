package com.crpc.core.filter.server;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.ServerServiceSemaphoreWrapper;
import com.crpc.core.common.annotations.SPI;
import com.crpc.core.common.exception.MaxServiceLimitRequestException;
import com.crpc.core.filter.ServerFilter;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Semaphore;

import static com.crpc.core.common.cache.CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP;


/**
 * 服务端方法限流过滤器
 *
 * @author cong
 * @date 2024/01/31
 */
@SPI("before")
@Slf4j
public class ServerServiceBeforeLimitFilterImpl implements ServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        //从缓存中提取semaphore对象
        Semaphore semaphore = serverServiceSemaphoreWrapper.getSemaphore();
        boolean tryResult = semaphore.tryAcquire();
        if (!tryResult) {
            log.error("[ServerServiceBeforeLimitFilterImpl] {}'s max request is {},reject now", rpcInvocation.getTargetServiceName(), serverServiceSemaphoreWrapper.getMaxNums());
            MaxServiceLimitRequestException iRpcException = new MaxServiceLimitRequestException(rpcInvocation);
            rpcInvocation.setE(iRpcException);
            throw iRpcException;
        }
    }
}
