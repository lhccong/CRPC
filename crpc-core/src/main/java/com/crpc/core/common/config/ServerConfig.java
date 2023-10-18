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

    /**
     * 服务端序列化方式 example: hession2,kryo,jdk,fastjson
     */
    private String serverSerialize;


}
