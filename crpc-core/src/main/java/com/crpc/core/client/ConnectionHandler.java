package com.crpc.core.client;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.router.CRouter;
import com.crpc.core.router.Selector;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.crpc.core.common.cache.CommonClientCache.*;

/**
 * 连接处理器
 * 职责： 当注册中心的节点新增或者移除或者权重变化的时候，这个类主要负责对内存中的url做变更
 *
 * @author liuhuaicong
 * @date 2023/08/14
 */
public class ConnectionHandler {

    /**
     * 核心的连接处理器
     * 专门用于负责和服务端构建链接通信
     */
    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap) {
        ConnectionHandler.bootstrap = bootstrap;
    }

    /**
     * 构建单个连接通道 元操作，既要处理连接，还要统一将连接进行内存存储管理
     *
     * @param providerServiceName 提供者服务名称
     * @param providerIp          提供知识产权
     * @throws InterruptedException 中断异常
     */
    public static void connect(String providerServiceName, String providerIp) throws InterruptedException {
        if (bootstrap == null) {
            throw new IllegalArgumentException("bootstrap can not be null");
        }
        //格式错误类型的信息
        if (!providerIp.contains(":")) {
            return;
        }
        String[] providerAddress = providerIp.split(":");
        //地址跟端口号
        String ip = providerAddress[0];
        int port = Integer.parseInt(providerAddress[1]);
        //到底这个channelFuture里面是什么
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
        channelFutureWrapper.setChannelFuture(channelFuture);
        channelFutureWrapper.setHost(ip);
        channelFutureWrapper.setPort(port);
        SERVER_ADDRESS.add(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            channelFutureWrappers = new ArrayList<>();
        }
        channelFutureWrappers.add(channelFutureWrapper);
        CONNECT_MAP.put(providerServiceName, channelFutureWrappers);
        Selector selector = new Selector();
        selector.setProviderServiceName(providerServiceName);
        CROUTER.refreshRouteArr(selector);
    }

    /**
     * 创建ChannelFuture
     *
     * @param ip   地址
     * @param port 端口
     * @return {@link ChannelFuture}
     * @throws InterruptedException 中断异常
     */
    public static ChannelFuture createChannelFuture(String ip, Integer port) throws InterruptedException {
        return bootstrap.connect(ip, port).sync();
    }

    /**
     * 断开连接
     *
     * @param providerServiceName 提供者服务名称
     * @param providerIp          提供知识产权
     */
    public static void disConnect(String providerServiceName, String providerIp) {
        SERVER_ADDRESS.remove(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isNotEmptyList(channelFutureWrappers)) {
            channelFutureWrappers.removeIf(channelFutureWrapper -> providerIp.equals(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort()));
        }
    }

    /**
     * 默认走随机策略获取ChannelFuture
     *
     * @param providerServiceName 提供者服务名称
     * @return {@link ChannelFuture}
     */
    public static ChannelFuture getChannelFuture(String providerServiceName) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            throw new IllegalArgumentException("no provider exist for " + providerServiceName);
        }
        return channelFutureWrappers.get(new Random().nextInt(channelFutureWrappers.size())).getChannelFuture();
    }
}
