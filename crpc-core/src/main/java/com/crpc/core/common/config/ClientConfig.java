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

    private String proxyType;


}
