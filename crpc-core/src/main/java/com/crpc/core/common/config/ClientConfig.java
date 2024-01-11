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

    /**
     * 应用程序名称
     */
    private String applicationName;

    /**
     * 注册地址
     */
    private String registerAddr;

    /**
     * 注册类型
     */
    private String registerType;
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
