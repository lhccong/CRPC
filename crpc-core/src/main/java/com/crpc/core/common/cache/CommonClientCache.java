package com.crpc.core.common.cache;


import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.ChannelFuturePollingRef;
import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.config.ClientConfig;
import com.crpc.core.filter.client.ClientFilterChain;
import com.crpc.core.registry.URL;
import com.crpc.core.registry.zookeeper.AbstractRegister;
import com.crpc.core.router.CRouter;
import com.crpc.core.serialize.SerializeFactory;
import com.crpc.core.spi.ExtensionLoader;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 公用缓存 存储请求队列等公共信息
 *
 * @author liuhuaicong
 * @date 2023/08/09
 */
public class CommonClientCache {
    private CommonClientCache() {
        throw new IllegalStateException("Utility class");
    }
    //要发送的调用信息
    public static  BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);

    //远程调用结果
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();

    //provider名称 --> 该服务有哪些集群URL
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();

    public static ClientConfig CLIENT_CONFIG;

    public static Map<String, Map<String,String>> URL_MAP = new ConcurrentHashMap<>();

    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    //每次进行远程调用的时候都是从这里面去选择服务提供者
    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();
    //随机请求的map
    public static Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();
    public static ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();
    public static CRouter CROUTER;

    public static SerializeFactory CLIENT_SERIALIZE_FACTORY;


    public static ClientFilterChain CLIENT_FILTER_CHAIN ;

    public static AbstractRegister ABSTRACT_REGISTER;

    public static ExtensionLoader EXTENSION_LOADER = new ExtensionLoader();
}
