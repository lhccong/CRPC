package com.crpc.core.common.event.listener;

import com.crpc.core.client.ConnectionHandler;
import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.common.event.CRpcEvent;
import com.crpc.core.common.event.CRpcListener;
import com.crpc.core.common.event.URLChangeWrapper;
import com.crpc.core.common.utils.CommonUtils;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.crpc.core.common.cache.CommonClientCache.CONNECT_MAP;

/**
 * 服务更新侦听器
 *
 * @author liuhuaicong
 * @date 2023/08/14
 */
public class ServiceUpdateListener implements CRpcListener<CRpcEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUpdateListener.class);

    @Override
    public void callBack(Object t) {
        //获取到子节点的数据信息
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) t;
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(urlChangeWrapper.getServiceName());
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            LOGGER.error("[ServiceUpdateListener] channelFutureWrappers is empty");
        } else {
            List<String> matchProviderUrl = urlChangeWrapper.getProviderUrl();
            Set<String> finalUrl = new HashSet<>();
            List<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();
            for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
                String oldServerAddress = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
                //如果旧的url没有就说明已被移除
                if (matchProviderUrl.contains(oldServerAddress)) {
                    finalChannelFutureWrappers.add(channelFutureWrapper);
                    finalUrl.add(oldServerAddress);
                }
            }
            //此时老的url已经被移除，开始检查是否有新的url
            List<ChannelFutureWrapper> newChannelFutureWrappers = new ArrayList<>();
            for (String newProviderUrl : matchProviderUrl) {
                if (!finalUrl.contains(newProviderUrl)) {
                    ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
                    //分割出地址、端口号
                    String host = newProviderUrl.split(":")[0];
                    Integer port = Integer.valueOf(newProviderUrl.split(":")[1]);

                    channelFutureWrapper.setHost(host);
                    channelFutureWrapper.setPort(port);
                    ChannelFuture channelFuture;
                    try {
                        channelFuture = ConnectionHandler.createChannelFuture(host, port);
                        channelFutureWrapper.setChannelFuture(channelFuture);
                        newChannelFutureWrappers.add(channelFutureWrapper);
                        finalUrl.add(newProviderUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finalChannelFutureWrappers.addAll(newChannelFutureWrappers);
                    //更新服务
                    CONNECT_MAP.put(urlChangeWrapper.getServiceName(), finalChannelFutureWrappers);
                }
            }
        }
    }
}
