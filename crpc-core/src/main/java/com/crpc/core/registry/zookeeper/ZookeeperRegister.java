package com.crpc.core.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.crpc.core.common.event.CRpcEvent;
import com.crpc.core.common.event.CRpcListenerLoader;
import com.crpc.core.common.event.CRpcNodeChangeEvent;
import com.crpc.core.common.event.CRpcUpdateEvent;
import com.crpc.core.common.event.data.URLChangeWrapper;
import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;
import com.crpc.interfaces.DataService;

import java.util.List;

/**
 * Zookeeper注册类
 *
 * @author liuhuaicong
 * @date 2023/08/11
 */

public class ZookeeperRegister extends AbstractRegister implements RegistryService {

    private AbstractZookeeperClient zkClient;

    private String ROOT = "/crpc";

    public ZookeeperRegister(String address) {
        this.zkClient = new CuratorZookeeperClient(address);
    }

    private String getProviderPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/provider/" + url.getParameters().get("host") + ":" + url.getParameters().get("port");
    }

    private String getConsumerPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/consumer/" + url.getApplicationName() + ":" + url.getParameters().get("host") + ":";
    }

    @Override
    public void register(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }
        String urlStr = URL.buildProviderUrlStr(url);
        String providerPath = getProviderPath(url);
        if (!zkClient.existNode(providerPath)) {
            zkClient.createTemporaryData(providerPath, urlStr);
        } else {
            zkClient.deleteNode(providerPath);
            zkClient.createTemporaryData(providerPath, urlStr);
        }
        super.register(url);
    }

    @Override
    public void unRegister(URL url) {
        zkClient.deleteNode(getProviderPath(url));
        super.unRegister(url);
    }

    @Override
    public void subscribe(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }
        String urlStr = URL.buildConsumerUrlStr(url);
        String consumerPath = getConsumerPath(url);
        if (!zkClient.existNode(consumerPath)) {
            zkClient.createTemporarySeqData(consumerPath, urlStr);
        } else {
            zkClient.deleteNode(consumerPath);
            zkClient.createTemporarySeqData(consumerPath, urlStr);
        }
        super.subscribe(url);
    }

    @Override
    public void doUnSubscribe(URL url) {
        zkClient.deleteNode(getConsumerPath(url));
        super.doUnSubscribe(url);
    }

    @Override
    public void doBeforeSubscribe(URL url) {

    }

    @Override
    public void doAfterSubscribe(URL url) {
        String servicePath = url.getParameters().get("servicePath");
        //监听是否由新的服务注册
        String newServerNodePath = ROOT + "/"  + servicePath;
        watchChildNodeData(newServerNodePath);
        String providerIpStrJson = url.getParameters().get("providerIps");
        List<String> providerIpList = JSON.parseObject(providerIpStrJson, List.class);

        for (String providerIp : providerIpList) {
            this.watchNodeDataChange(ROOT + "/" + servicePath + "/" + providerIp);
        }

    }

    /**
     * 订阅服务节点内部的数据变化
     *
     * @param newServerNodePath 新服务器节点路径
     */
    public void watchNodeDataChange(String newServerNodePath){
        zkClient.watchNodeData(newServerNodePath, watchedEvent -> {
            String path = watchedEvent.getPath();
            String nodeData = zkClient.getNodeData(path);
            nodeData = nodeData.replace(";", "/");
            ProviderNodeInfo providerNodeInfo = URL.buildUrlFromUrlStr(nodeData);
            CRpcEvent cRpcEvent = new CRpcNodeChangeEvent(providerNodeInfo);
            CRpcListenerLoader.sendEvent(cRpcEvent);

            //收到回调之后在注册一次监听，这样能保证一直都收到消息
            watchChildNodeData(newServerNodePath);
        });
    }

    public void watchChildNodeData(String newServerNodePath) {
        zkClient.watchChildNodeData(newServerNodePath, watchedEvent -> {
            String path = watchedEvent.getPath();
            List<String> childrenDataList = zkClient.getChildrenData(path);
            URLChangeWrapper urlChangeWrapper = new URLChangeWrapper();
            urlChangeWrapper.setProviderUrl(childrenDataList);
            urlChangeWrapper.setServiceName(path.split("/")[2]);
            //自定义的一套事件监听组件
            CRpcEvent cRpcEvent = new CRpcUpdateEvent(urlChangeWrapper);
            CRpcListenerLoader.sendEvent(cRpcEvent);
            //收到回调之后再注册一次监听，这样能保证一直收到消息
            watchChildNodeData(path);
        });
    }

    @Override
    public List<String> getProviderIps(String serviceName) {
        return this.zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
    }

    public static void main(String[] args) throws InterruptedException {
        ZookeeperRegister zookeeperRegister = new ZookeeperRegister("localhost:2181");
        List<String> urls = zookeeperRegister.getProviderIps(DataService.class.getName());
        System.out.println(urls);
        Thread.sleep(2000000);
    }
}
