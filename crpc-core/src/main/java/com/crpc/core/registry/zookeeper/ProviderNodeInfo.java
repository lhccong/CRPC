package com.crpc.core.registry.zookeeper;


import lombok.Data;
import lombok.ToString;

/**
 * 提供者节点信息
 *
 * @author liuhuaicong
 * @date 2023/08/10
 */
@Data
@ToString
public class ProviderNodeInfo {
    /**
     * 应用程序名称
     */
    private String applicationName;
    /**
     * 服务名称
     */

    private String serviceName;
    /**
     * 服务地址
     */
    private String address;
    /**
     * 权重
     */
    private Integer weight;

    /**
     * 注册时间
     */
    private String registryTime;

    /**
     * 群
     */
    private String group;

}
