package com.crpc.core.common.event.listener;

import com.crpc.core.client.ConnectionHandler;
import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.event.CRpcEvent;
import com.crpc.core.common.event.CRpcUpdateEvent;
import com.crpc.core.common.event.data.URLChangeWrapper;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.registry.URL;
import com.crpc.core.registry.zookeeper.ProviderNodeInfo;
import com.crpc.core.router.Selector;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.crpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static com.crpc.core.common.cache.CommonClientCache.CROUTER;

/**
 * 服务更新侦听器
 *
 * @author liuhuaicong
 * @date 2023/08/14
 */
@Slf4j
public class ServiceUpdateListener implements CRpcListener<CRpcUpdateEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUpdateListener.class);


    /**
     * 回调函数，用于处理子节点的数据信息
     *
     * @param t 子节点的数据信息
     */
    @Override
    public void callBack(Object t) {
        //获取到子节点的数据信息
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) t;
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(urlChangeWrapper.getServiceName());

        List<String> matchProviderUrl = urlChangeWrapper.getProviderUrl();
        Set<String> finalUrl = new HashSet<>();
        List<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();
        if (channelFutureWrappers!=null&&!channelFutureWrappers.isEmpty()){
            // 遍历channelFutureWrappers，筛选出与matchProviderUrl匹配的节点
            for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
                String oldServerAddress = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
                if (matchProviderUrl.contains(oldServerAddress)) {
                    finalChannelFutureWrappers.add(channelFutureWrapper);
                    finalUrl.add(oldServerAddress);
                }
            }
        }


        // 此时老的url已经被移除，开始检查是否有新的url
        List<ChannelFutureWrapper> newChannelFutureWrappers = new ArrayList<>();

        // 遍历matchProviderUrl，添加新的节点到newChannelFutureWrappers
        for (String newProviderUrl : matchProviderUrl) {
            if (!finalUrl.contains(newProviderUrl)) {
                ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();

                // 分割出地址、端口号
                String host = newProviderUrl.split(":")[0];
                Integer port = Integer.valueOf(newProviderUrl.split(":")[1]);

                channelFutureWrapper.setHost(host);
                channelFutureWrapper.setPort(port);

                // 从urlChangeWrapper获取节点数据url，通过URL.buildURLFromUrlStr方法构建ProviderNodeInfo对象
                String urlStr = urlChangeWrapper.getNodeDataUrl().get(newProviderUrl);
                ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(urlStr);

                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
                channelFutureWrapper.setGroup(providerNodeInfo.getGroup());

                ChannelFuture channelFuture;
                try {
                    // 创建通道连接
                    channelFuture = ConnectionHandler.createChannelFuture(host, port);
                    channelFutureWrapper.setChannelFuture(channelFuture);
                    newChannelFutureWrappers.add(channelFutureWrapper);
                    finalUrl.add(newProviderUrl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

                // 将newChannelFutureWrappers添加到finalChannelFutureWrappers中
                finalChannelFutureWrappers.addAll(newChannelFutureWrappers);

                // 更新服务
                log.info("我要更新服务啦");
                CONNECT_MAP.put(urlChangeWrapper.getServiceName(), finalChannelFutureWrappers);
                Selector selector = new Selector();
                selector.setProviderServiceName(urlChangeWrapper.getServiceName());
                CROUTER.refreshRouteArr(selector);
            }
        }
    }
}

