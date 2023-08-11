package com.crpc.core.common;

import io.netty.channel.ChannelFuture;
import lombok.Data;


/**
 *
 *
 * @author liuhuaicong
 * @date 2023/08/10
 */
@Data
public class ChannelFutureWrapper {

    private ChannelFuture channelFuture;

    private String host;

    private Integer port;

}
