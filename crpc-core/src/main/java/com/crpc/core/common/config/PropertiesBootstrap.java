package com.crpc.core.common.config;

import sun.applet.AppletIllegalArgumentException;

import java.io.IOException;

import static com.crpc.core.common.constants.RpcConstants.*;

/**
 * 属性配置
 *
 * @author liuhuaicong
 * @date 2023/08/18
 */
public class PropertiesBootstrap {
    private volatile boolean configIsReady;
    public static final String SERVER_PORT = "crpc.serverPort";
    public static final String REGISTER_ADDRESS = "crpc.registerAddr";
    public static final String APPLICATION_NAME = "crpc.applicationName";
    public static final String PROXY_TYPE = "crpc.proxyType";
    public static final String ROUTER_TYPE = "crpc.routerStrategy";
    public static final String SERVER_SERIALIZE_TYPE = "crpc.serverSerialize";
    public static final String CLIENT_SERIALIZE_TYPE = "crpc.clientSerialize";

    public static ServerConfig loadServerConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadServerConfigFromLocal fail,e is {}", e);
        }
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStrDefault(SERVER_SERIALIZE_TYPE,JDK_SERIALIZE_TYPE));
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadClientConfigFromLocal fail,e is {}", e);
        }
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        clientConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStrDefault(PROXY_TYPE,JDK_PROXY_TYPE));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStrDefault(ROUTER_TYPE,RANDOM_ROUTER_TYPE));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStrDefault(CLIENT_SERIALIZE_TYPE,JDK_SERIALIZE_TYPE));
        return clientConfig;
    }
}
