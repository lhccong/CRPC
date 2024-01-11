package com.crpc.core.router;

import com.crpc.core.common.ChannelFutureWrapper;
import lombok.Data;

/**
 * 选择器
 *
 * @author liuhuaicong
 * @date 2023/08/22
 */
@Data
public class Selector {

    /**
     * 提供者服务名称
     * eg: com.cong.test.DataService
     */
    private String providerServiceName;

    /**
     * 经过二次筛选之后的future集合
     */
    private ChannelFutureWrapper[] channelFutureWrappers;
}
