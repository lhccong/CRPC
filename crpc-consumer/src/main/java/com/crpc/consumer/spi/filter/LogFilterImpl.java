package com.crpc.consumer.spi.filter;


import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.filter.ClientFilter;

import java.util.List;

/**
 * 日志过滤器 impl
 *
 * @author cong
 * @date 2024/01/12
 */
public class LogFilterImpl implements ClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        System.out.println("this is a test");
    }
}
