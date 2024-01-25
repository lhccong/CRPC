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

    /**
     * 服务器端口
     */
    private Integer serverPort;

    /**
     * 注册地址
     */
    private String registerAddr;

    /**
     * 注册类型
     */
    private String registerType;

    /**
     * 应用程序名称
     */
    private String applicationName;

    /**
     * 服务端序列化方式 example: hession2,kryo,jdk,fastjson
     */
    private String serverSerialize;

    /**
     * 服务端业务线程数目
     */
    private Integer serverBizThreadNums;

    /**
     * 服务端接收队列的大小
     */
    private Integer serverQueueSize;

}
