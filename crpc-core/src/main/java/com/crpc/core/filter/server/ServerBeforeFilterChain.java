package com.crpc.core.filter.server;

import com.crpc.core.common.RpcInvocation;
import com.crpc.core.filter.ServerFilter;


import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器链前服务器
 *
 * @author cong
 * @date 2024/01/31
 */
public class ServerBeforeFilterChain {

    private static final List<ServerFilter> SERVER_FILTERS = new ArrayList<>();

    public void addServerFilter(ServerFilter iServerFilter) {
        SERVER_FILTERS.add(iServerFilter);
    }

    public void doFilter(RpcInvocation rpcInvocation) {
        for (ServerFilter iServerFilter : SERVER_FILTERS) {
            iServerFilter.doFilter(rpcInvocation);
        }
    }
}
