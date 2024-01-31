package com.crpc.core.dispatcher;

import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.RpcProtocol;
import com.crpc.core.server.ServerChannelReadData;

import java.lang.reflect.Method;
import java.util.concurrent.*;

import static com.crpc.core.common.cache.CommonServerCache.*;

/**
 * 服务器通道调度程序
 *
 * @author cong
 * @date 2024/01/25
 */
public class ServerChannelDispatcher {
    private BlockingQueue<ServerChannelReadData> RPC_DATA_QUEUE;

    private ExecutorService executorService;

    public ServerChannelDispatcher() {
        // 初始化使用无参构造
    }

    public void init(int queueSize, int bizThreadNums) {
        RPC_DATA_QUEUE = new ArrayBlockingQueue<>(queueSize);
        executorService = new ThreadPoolExecutor(bizThreadNums, bizThreadNums,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(512));
    }

    public void add(ServerChannelReadData serverChannelReadData) {
        RPC_DATA_QUEUE.add(serverChannelReadData);
    }

    class ServerJobCoreHandle implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
                    executorService.submit(() -> {
                            RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                            RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);
                            //执行过滤链路
                            SERVER_FILTER_CHAIN.doFilter(rpcInvocation);
                            Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                            Method[] methods = aimObject.getClass().getDeclaredMethods();
                            Object result = null;
                            for (Method method : methods) {
                                if (method.getName().equals(rpcInvocation.getTargetMethod())) {
                                    if (method.getReturnType().equals(Void.TYPE)) {
                                        try {
                                            method.invoke(aimObject, rpcInvocation.getArgs());
                                        } catch (Exception e) {
                                            //业务异常
                                            rpcInvocation.setE(e);
                                        }
                                    } else {
                                        try {
                                            result = method.invoke(aimObject, rpcInvocation.getArgs());
                                        } catch (Exception e) {
                                            //业务异常
                                            rpcInvocation.setE(e);
                                        }
                                    }
                                    break;
                                }
                            }
                            rpcInvocation.setResponse(result);
                            RpcProtocol respRpcProtocol = new RpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
                            serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void startDataConsume() {
        Thread thread = new Thread(new ServerJobCoreHandle());
        thread.start();
    }
}
