package com.crpc.core.common.cache;


import com.crpc.core.RpcInvocation;
import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.config.ClientConfig;
import com.crpc.core.registry.URL;

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
    //要发送的调用信息
    public static  BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);

    //远程调用结果
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();

    //provider名称 --> 该服务有哪些集群URL
    public static List<String> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();

    public static ClientConfig CLIENT_CONFIG;

    public static Map<String, List<URL>> URL_MAP = new ConcurrentHashMap<>();

    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    //每次进行远程调用的时候都是从这里面去选择服务提供者
    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();
}
