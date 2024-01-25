package com.crpc.core.common.config;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import static com.crpc.core.common.constants.RpcConstants.*;

/**
 * 属性配置
 *
 * @author liuhuaicong
 * @date 2023/08/18
 */
@Slf4j
public class PropertiesBootstrap {
    private volatile boolean configIsReady;
    public static final String SERVER_PORT = "crpc.serverPort";
    public static final String REGISTER_ADDRESS = "crpc.registerAddr";

    public static final String REGISTER_TYPE = "crpc.registerType";
    public static final String APPLICATION_NAME = "crpc.applicationName";
    public static final String PROXY_TYPE = "crpc.proxyType";
    public static final String ROUTER_TYPE = "crpc.routerStrategy";
    public static final String SERVER_SERIALIZE_TYPE = "crpc.serverSerialize";
    public static final String CLIENT_SERIALIZE_TYPE = "crpc.clientSerialize";
    public static final String CLIENT_DEFAULT_TIME_OUT = "crpc.client.default.timeout";
    public static final String SERVER_BIZ_THREAD_NUMS = "crpc.server.biz.thread.nums";
    public static final String SERVER_QUEUE_SIZE = "crpc.server.queue.size";

    public static ServerConfig loadServerConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            log.error("loadServerConfigFromLocal fail,e is {}", e.getMessage());
        }
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        serverConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStrDefault(SERVER_SERIALIZE_TYPE,JDK_SERIALIZE_TYPE));
        serverConfig.setServerBizThreadNums(PropertiesLoader.getPropertiesIntegerDefault(SERVER_BIZ_THREAD_NUMS,DEFAULT_THREAD_NUMS));
        serverConfig.setServerQueueSize(PropertiesLoader.getPropertiesIntegerDefault(SERVER_QUEUE_SIZE,DEFAULT_QUEUE_SIZE));
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            log.error("loadClientConfigFromLocal fail,e is {}", e.getMessage());
        }
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesNotBlank(APPLICATION_NAME));
        clientConfig.setRegisterAddr(PropertiesLoader.getPropertiesNotBlank(REGISTER_ADDRESS));
        clientConfig.setRegisterType(PropertiesLoader.getPropertiesNotBlank(REGISTER_TYPE));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStrDefault(PROXY_TYPE,JDK_PROXY_TYPE));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStrDefault(ROUTER_TYPE,RANDOM_ROUTER_TYPE));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStrDefault(CLIENT_SERIALIZE_TYPE,JDK_SERIALIZE_TYPE));
        clientConfig.setTimeOut(PropertiesLoader.getPropertiesIntegerDefault(CLIENT_DEFAULT_TIME_OUT,DEFAULT_TIMEOUT));
        return clientConfig;
    }
}
