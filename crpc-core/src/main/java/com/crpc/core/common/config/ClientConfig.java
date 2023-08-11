package com.crpc.core.common.config;


/**
 * 客户端配置
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public class ClientConfig {

    private Integer port;

    private String serverAddr;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
