package com.crpc.core.filter;



import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.RpcInvocation;

import java.util.List;


/**
 * 客户端筛选器
 *
 * @author liuhuaicong
 * @date 2023/10/20
 */
public interface ClientFilter extends CFilter {

    /**
     * 执行过滤链
     *
     * @param src           来源
     * @param rpcInvocation RPC 调用
     */
    void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation);
}
