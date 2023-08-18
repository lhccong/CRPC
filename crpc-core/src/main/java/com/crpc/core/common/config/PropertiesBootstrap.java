package com.crpc.core.common.config;

import sun.applet.AppletIllegalArgumentException;

import java.io.IOException;

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
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStr(PROXY_TYPE));
        return clientConfig;
    }
}
