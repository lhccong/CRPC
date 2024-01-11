package com.crpc.core.filter;


import com.crpc.core.common.RpcInvocation;


/**
 * 服务器筛选器
 *
 * @author liuhuaicong
 * @date 2023/10/20
 */
public interface ServerFilter extends CFilter {

    /**
     * 执行核心过滤逻辑
     *
     * @param rpcInvocation RPC 调用
     */
    void doFilter(RpcInvocation rpcInvocation);
}
