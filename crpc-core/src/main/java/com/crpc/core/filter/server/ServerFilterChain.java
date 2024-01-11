package com.crpc.core.filter.server;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.filter.ServerFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端筛选器链
 *
 * @author liuhuaicong
 * @date 2023/10/20
 */
public class ServerFilterChain {
    public static final List<ServerFilter> SERVER_FILTER_LIST = new ArrayList<>();

    public void addServerFilter(ServerFilter clientFilter){
        SERVER_FILTER_LIST.add(clientFilter);
    }

    public void doFilter(RpcInvocation rpcInvocation){
        for (ServerFilter clientFilter : SERVER_FILTER_LIST) {
            clientFilter.doFilter(rpcInvocation);
        }
    }

}
