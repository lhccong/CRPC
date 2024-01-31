package com.crpc.core.common.exception;


import com.crpc.core.common.RpcInvocation;

/**
 * CRPC 异常
 *
 * @author cong
 * @date 2024/01/31
 */
public class CRpcException extends RuntimeException {

    private RpcInvocation rpcInvocation;

    public RpcInvocation getRpcInvocation() {
        return rpcInvocation;
    }

    public void setRpcInvocation(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }

    public CRpcException(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }

}
