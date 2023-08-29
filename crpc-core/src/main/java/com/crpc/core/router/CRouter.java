package com.crpc.core.router;

import com.crpc.core.common.ChannelFutureWrapper;
import com.crpc.core.registry.URL;


/**
 *
 *
 * @author liuhuaicong
 * @date 2023/08/22
 */
public interface CRouter {

    /**
     * 刷新路由数组
     *
     * @param selector 选择器
     */
    void refreshRouteArr(Selector selector);

    /**
     * 获取请求连接通道
     *
     * @param selector 选择器
     * @return {@link ChannelFutureWrapper}
     */
    ChannelFutureWrapper select(Selector selector);

    /**
     * 更新权重
     *
     * @param url url
     */
    void updateWeight(URL url);
}
