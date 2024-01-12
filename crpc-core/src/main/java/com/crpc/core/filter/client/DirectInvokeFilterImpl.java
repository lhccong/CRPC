package com.crpc.core.filter.client;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.filter.ClientFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;


/**
 * 直连过滤器
 *
 * @author liuhuaicong
 * @date 2023/10/25
 */
@Slf4j
public class DirectInvokeFilterImpl implements ClientFilter {

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String url = (String) rpcInvocation.getAttachments().get("url");
        if(CommonUtils.isEmpty(url)){
            return;
        }
        src.removeIf(channelFutureWrapper -> !(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort()).equals(url));
        if(CommonUtils.isEmptyList(src)){
            log.error("no match provider url for "+ url);
        }
    }
}
