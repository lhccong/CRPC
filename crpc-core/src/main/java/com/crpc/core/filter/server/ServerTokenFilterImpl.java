package com.crpc.core.filter.server;

import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.annotations.SPI;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.filter.ServerFilter;
import com.crpc.core.server.ServiceWrapper;
import lombok.extern.slf4j.Slf4j;

import static com.crpc.core.common.cache.CommonServerCache.PROVIDER_SERVICE_WRAPPER_MAP;


/**
 * 简单版本的token校验
 *
 * @author liuhuaicong
 * @date 2023/10/25
 */
@Slf4j
@SPI("before")
public class ServerTokenFilterImpl implements ServerFilter {

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String token = String.valueOf(rpcInvocation.getAttachments().get("serviceToken"));
        ServiceWrapper serviceWrapper = PROVIDER_SERVICE_WRAPPER_MAP.get(rpcInvocation.getTargetServiceName());
        String matchToken = String.valueOf(serviceWrapper.getServiceToken());
        if (CommonUtils.isEmpty(matchToken)) {
            return;
        }
        if (!CommonUtils.isEmpty(token) && token.equals(matchToken)) {
            return;
        }
        log.error("token is " + token + " , verify result is false!");
    }
}
