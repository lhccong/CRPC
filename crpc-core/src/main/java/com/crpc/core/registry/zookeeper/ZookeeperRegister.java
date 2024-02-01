package com.crpc.core.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.crpc.core.common.event.CRpcEvent;
import com.crpc.core.common.event.CRpcListenerLoader;
import com.crpc.core.common.event.CRpcNodeChangeEvent;
import com.crpc.core.common.event.CRpcUpdateEvent;
import com.crpc.core.common.event.data.URLChangeWrapper;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;
import com.crpc.interfaces.DataService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.crpc.core.common.cache.CommonClientCache.CLIENT_CONFIG;
import static com.crpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static com.crpc.core.common.cache.CommonServerCache.IS_STARTED;
import static com.crpc.core.common.cache.CommonServerCache.SERVER_CONFIG;

/**
 * Zookeeper注册类
 *
 * @author liuhuaicong
 * @date 2023/08/11
 */
@Slf4j

public class ZookeeperRegister extends AbstractRegister implements RegistryService {

    private final AbstractZookeeperClient zkClient;

    private final String ROOT = "/crpc";

    public ZookeeperRegister(String address) {
        this.zkClient = new CuratorZookeeperClient(address);
    }

    public ZookeeperRegister() {
        String registryAddr = CLIENT_CONFIG != null ? CLIENT_CONFIG.getRegisterAddr() : SERVER_CONFIG.getRegisterAddr();
        this.zkClient = new CuratorZookeeperClient(registryAddr);
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
        if (!IS_STARTED) {
            return;
        }
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
        String newServerNodePath = ROOT + "/" + servicePath;
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
    public void watchNodeDataChange(String newServerNodePath) {
        zkClient.watchNodeData(newServerNodePath, watchedEvent -> {
            String path = watchedEvent.getPath();
            log.info("[watchNodeDataChange] 监听到zk节点下的{}节点数据发生变更", path);
            String nodeData = zkClient.getNodeData(path);
            ProviderNodeInfo providerNodeInfo;
            if (StringUtil.isNullOrEmpty(nodeData)) {
                String[] split = path.split("/");
                String serviceName = split[split.length - 3];
                CONNECT_MAP.remove(serviceName);
                return;
            }
            providerNodeInfo = URL.buildURLFromUrlStr(nodeData);
            CRpcNodeChangeEvent cRpcEvent = new CRpcNodeChangeEvent(providerNodeInfo);
            CRpcListenerLoader.sendEvent(cRpcEvent);
            //收到回调之后在注册一次监听，这样能保证一直都收到消息
            watchNodeDataChange(newServerNodePath);
        });
    }

    public void watchChildNodeData(String newServerNodePath) {
        zkClient.watchChildNodeData(newServerNodePath, watchedEvent -> {
            String path = watchedEvent.getPath();
            log.info("收到子节点" + path + "数据变化");
            List<String> childrenDataList = zkClient.getChildrenData(path);
            if (CommonUtils.isEmptyList(childrenDataList)) {
                watchChildNodeData(path);
                return;
            }
            URLChangeWrapper urlChangeWrapper = new URLChangeWrapper();
            Map<String, String> nodeDetailInfoMap = new HashMap<>();
            for (String providerAddress : childrenDataList) {
                String nodeDetailInfo = zkClient.getNodeData(path + "/" + providerAddress);
                nodeDetailInfoMap.put(providerAddress, nodeDetailInfo);
            }
            urlChangeWrapper.setNodeDataUrl(nodeDetailInfoMap);
            urlChangeWrapper.setProviderUrl(childrenDataList);
            urlChangeWrapper.setServiceName(path.split("/")[2]);
            CRpcEvent cRpcEvent = new CRpcUpdateEvent(urlChangeWrapper);
            CRpcListenerLoader.sendEvent(cRpcEvent);
            //收到回调之后再注册一次监听，这样能保证一直都收到消息
            watchChildNodeData(path);
            for (String providerAddress : childrenDataList) {
                watchNodeDataChange(path + "/" + providerAddress);
            }
        });
    }

    @Override
    public List<String> getProviderIps(String serviceName) {
        return this.zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
    }

    @Override
    public Map<String, String> getServiceWeightMap(String serviceName) {
        List<String> nodeDataList = this.zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
        Map<String, String> result = new HashMap<>();
        for (String ipAndHost : nodeDataList) {
            String childData = this.zkClient.getNodeData(ROOT + "/" + serviceName + "/provider/" + ipAndHost);
            result.put(ipAndHost, childData);
        }
        return result;
    }

}
