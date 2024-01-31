package com.crpc.core.proxy.jdk;


import com.crpc.core.client.RpcReferenceWrapper;
import com.crpc.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.crpc.core.common.cache.CommonClientCache.RESP_MAP;
import static com.crpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static com.crpc.core.common.constants.RpcConstants.DEFAULT_TIMEOUT;

/**
 * 各种代理工厂统一使用这个InvocationHandler
 *
 * @Author cong
 * @Date created in 6:59 下午 2021/12/5
 */
public class JDKClientInvocationHandler implements InvocationHandler {

    private static final Object OBJECT = new Object();

    private RpcReferenceWrapper rpcReferenceWrapper;
    private int timeOut = DEFAULT_TIMEOUT;

    public JDKClientInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {
        this.rpcReferenceWrapper = rpcReferenceWrapper;
        if (rpcReferenceWrapper.getAttatchments().containsKey("timeOut")) {
            timeOut = Integer.parseInt(String.valueOf(rpcReferenceWrapper.getAttatchments().get("timeOut")));
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(rpcReferenceWrapper.getAimClass().getName());
        //这里面注入了一个uuid，对每一次的请求都做单独区分
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttatchments());
        rpcInvocation.setRetry(rpcReferenceWrapper.getRetry());
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
        //这里就是将请求的参数放入到发送队列中
        SEND_QUEUE.add(rpcInvocation);
        if (rpcReferenceWrapper.isAsync()) {
            return null;
        }
        long beginTime = System.currentTimeMillis();
        int retryTimes = 0;
        //判断是否出现了超时异常 或者 是否设置了重置次数
        //客户端请求超时的一个判断依据
        while (System.currentTimeMillis() - beginTime < timeOut || rpcInvocation.getRetry() <= retryTimes) {
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if (!(object instanceof RpcInvocation)) {
                continue;
            }
            RpcInvocation rpcInvocationResp = (RpcInvocation) object;
            //正常结果
            if (rpcInvocationResp.getE() == null) {
                return rpcInvocationResp.getResponse();
            } else {
                //每次重试之后都会将retry值扣减1
                if (rpcInvocationResp.getRetry() == 0) {
                    return rpcInvocationResp.getResponse();
                }
                //如果是因为超时的情况，才会触发重试规则，否则重试机制不生效
                if (System.currentTimeMillis() - beginTime > timeOut) {
                    retryTimes++;
                    //重新请求
                    rpcInvocation.setResponse(null);
                    rpcInvocation.setRetry(rpcInvocationResp.getRetry() - 1);
                    RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
                    SEND_QUEUE.add(rpcInvocation);
                }
            }
        }
        throw new TimeoutException("Wait for response from server on client " + timeOut + "ms,retry times is " + retryTimes + ",service's name is " + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }
}
