package com.crpc.core.filter.client;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.filter.ClientFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 基于分组的过滤链路
 *
 * @author liuhuaicong
 * @date 2023/10/20
 */
@Slf4j
public class GroupFilterImpl implements ClientFilter {

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String group = String.valueOf(rpcInvocation.getAttachments().get("group"));
        src.removeIf(channelFutureWrapper -> !channelFutureWrapper.getGroup().equals(group));
        if (CommonUtils.isEmptyList(src)) {
            log.error("no provider match for group " + group);
        }
    }
}
