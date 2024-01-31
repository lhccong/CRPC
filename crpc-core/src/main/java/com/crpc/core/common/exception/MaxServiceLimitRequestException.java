package com.crpc.core.common.exception;


import com.crpc.core.common.RpcInvocation;

/**
 * 最大服务限制请求异常
 *
 * @author cong
 * @date 2024/01/31
 */
public class MaxServiceLimitRequestException extends CRpcException{

    public MaxServiceLimitRequestException(RpcInvocation rpcInvocation) {
        super(rpcInvocation);
    }
}
