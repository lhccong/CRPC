package com.crpc.core.registry.zookeeper;

import lombok.Data;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * 基于Zookeeper统一设计了一套模版抽象类
 *
 * @author liuhuaicong
 * @date 2023/08/10
 */
@Data
public abstract class AbstractZookeeperClient {

    /**
     * zk地址
     */
    private String zkAddress;


    private int baseSleepTimes;

    //最大重试次数
    private int maxRetryTimes;

    public AbstractZookeeperClient(String zkAddress){
        this.zkAddress  = zkAddress;

        //默认3000ms
        this.baseSleepTimes = 1000;
        this.maxRetryTimes = 3;
    }

    public AbstractZookeeperClient(String zkAddress,Integer baseSleepTimes,Integer maxRetryTimes){
        this.zkAddress  = zkAddress;

        if (baseSleepTimes == null) {
            this.baseSleepTimes = 1000;
        } else {
            this.baseSleepTimes = baseSleepTimes;
        }
        if (maxRetryTimes == null) {
            this.maxRetryTimes = 3;
        } else {
            this.maxRetryTimes = maxRetryTimes;
        }
    }


    /**
     * 更新节点数据
     *
     * @param address 地址
     * @param data    数据
     */
    public abstract void updateNodeData(String address,String data);

    /**
     *
     *
     * @return {@link Object}
     */
    public abstract Object getClient();

    /**
     * 得到节点数据
     *
     * @param path 路径
     * @return {@link String}
     */
    public abstract String getNodeData(String path);

    /**
     * 获取指定目录下的字节点数据
     *
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    public abstract List<String> getChildrenData(String path);

    /**
     * 创建持久类型节点数据信息
     *
     * @param address 地址
     * @param data    数据
     */
    public abstract void  createPersistentData(String address,String data);

    /**
     * 创建持久顺序类型节点数据信息
     *
     * @param address 地址
     * @param data    数据
     */
    public abstract void createPersistentWithSeqData(String address,String data);

    /**
     * 创建临时有序类型节点数据信息
     *
     * @param address 地址
     * @param data    数据
     */
    public abstract void createTemporarySeqData(String address,String data);


    /**
     * 创建临时节点数据类型信息
     *
     * @param address 地址
     * @param data    数据
     */
    public abstract void createTemporaryData(String address, String data);

    /**
     * 设置某个节点的数值
     *
     * @param address 地址
     * @param data    数据
     */
    public abstract void setTemporaryData(String address,String data);


    /**
     * 断开zk的客户端连接
     */
    public abstract void destroy();

    /**
     * 展示节点下面的数据
     *
     * @param address 地址
     * @return {@link List}<{@link String}>
     */
    public abstract List<String> listNode(String address);


    /**
     * 删除节点下面的数据
     *
     * @param address 地址
     * @return boolean
     */
    public abstract boolean deleteNode(String address);


    /**
     * 判断是否存在其他节点
     *
     * @param address 地址
     * @return boolean
     */
    public abstract boolean existNode(String address);

    /**
     * 监听path路径下某个节点的数据变化
     *
     * @param path    路径
     * @param watcher 观察家
     */
    public abstract void watchNodeData(String path, Watcher watcher);

    /**
     * 监听path路径下子节点的数据变化
     *
     * @param path    路径
     * @param watcher 观察家
     */
    public abstract void watchChildNodeData(String path, Watcher watcher);
}
