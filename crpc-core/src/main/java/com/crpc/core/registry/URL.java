package com.crpc.core.registry;


import com.crpc.core.registry.zookeeper.ProviderNodeInfo;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * url
 *
 * @author liuhuaicong
 * @date 2023/08/10
 */
@Data
public class URL {

    /**
     * 服务应用名称
     */
    private String applicationName;

    /**
     * 注册到节点到服务名称，例如：com.sise.test.UserService
     */
    private String serviceName;

    /**
     * 这里面可以自定义不限进行扩展
     * 分组
     * 权重
     * 服务提供者的地址
     * 服务提供者的端口
     */
    private Map<String, String> parameters = new HashMap<>();


    public void addParameter(String key, String value){
        this.parameters.putIfAbsent(key,value);
    }

    /**
     * 将URL转换为写入zk的provider节点下的一段字符串
     *
     * @param url url
     */
    public static String buildProviderUrlStr(URL url) {
        String host = url.getParameters().get("host");
        String port = url.getParameters().get("port");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";" + host + ":" + port + ";" + System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 将URL转换为写入zk的consumer节点下的一段字符串
     *
     * @param url url
     */
    public static String buildConsumerUrlStr(URL url) {
        String host = url.getParameters().get("host");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";" + host + ";" + System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
    }


    /**
     * 将某个节点下的信息转换为一个Provider节点对象
     *
     * @param providerNodeStr 提供者节点str
     * @return {@link ProviderNodeInfo}
     */
    public static ProviderNodeInfo buildUrlFromUrlStr(String providerNodeStr) {
        String[] items = providerNodeStr.split("/");
        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setServiceName(items[2]);
        providerNodeInfo.setAddress(items[4]);
        return providerNodeInfo;
    }


}
