package com.crpc.core.filter.client;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.filter.ClientFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端筛选器链
 *
 * @author liuhuaicong
 * @date 2023/10/20
 */
public class ClientFilterChain {
    public static final List<ClientFilter> CLIENT_FILTER_LIST = new ArrayList<>();

    public void addClientFilter(ClientFilter clientFilter){
        CLIENT_FILTER_LIST.add(clientFilter);
    }

    public void doFilter(List<ChannelFutureWrapper> src , RpcInvocation rpcInvocation){
        for (ClientFilter clientFilter : CLIENT_FILTER_LIST) {
            clientFilter.doFilter(src,rpcInvocation);
        }
    }

}
