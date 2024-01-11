package com.crpc.core.common.event.data;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;


/**
 *
 * @author liuhuaicong
 * @date 2023/08/29
 */
@Data
@ToString
public class URLChangeWrapper {

    private String serviceName;

    private List<String> providerUrl;

    /**
     * 节点数据 URL 记录每个ip下边的url详细信息，包括权重，分组等
     */
    private Map<String,String> nodeDataUrl;
}
