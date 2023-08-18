package com.crpc.core.common.config;

import lombok.Data;

/**
 * 服务器配置
 *
 * @author liuhuaicong
 * @date 2023/08/09
 */
@Data
public class ServerConfig {

    private Integer serverPort;

    private String registerAddr;

    private String applicationName;


}
