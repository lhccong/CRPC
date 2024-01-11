package com.crpc.core.filter.client;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.filter.ClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.crpc.core.common.cache.CommonClientCache.CLIENT_CONFIG;


/**
 客户端调用日志过滤器
 *
 * @author liuhuaicong
 * @date 2023/10/20
 */
public class ClientLogFilterImpl implements ClientFilter {

    private static Logger logger = LoggerFactory.getLogger(ClientLogFilterImpl.class);

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        rpcInvocation.getAttachments().put("c_app_name",CLIENT_CONFIG.getApplicationName());
        logger.info(rpcInvocation.getAttachments().get("c_app_name")+" do invoke -----> "+rpcInvocation.getTargetServiceName());
    }

}
