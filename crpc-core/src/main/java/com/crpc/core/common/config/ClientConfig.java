package com.crpc.core.common.config;


import lombok.Data;

/**
 * 客户端配置
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
@Data
public class ClientConfig {

    private String applicationName;

    private String registerAddr;
    /**
     * 代理类型 example: jdk,javassist
     */
    private String proxyType;
    /**
     * 负载均衡策略 example:random,rotate
     */
    private String routerStrategy;

    /**
     * 客户端序列化方式 example: hession2,kryo,jdk,fastjson
     */
    private String clientSerialize;

}
