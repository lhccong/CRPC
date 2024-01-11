package com.crpc.core.filter.server;

import com.crpc.core.common.RpcInvocation;
import com.crpc.core.filter.ServerFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 服务器日志筛选器
 *
 * @author liuhuaicong
 * @date 2023/10/25
 */
@Slf4j
public class ServerLogFilterImpl implements ServerFilter {


    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        log.info(rpcInvocation.getAttachments().get("c_app_name") + " do invoke -----> " + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }

}
