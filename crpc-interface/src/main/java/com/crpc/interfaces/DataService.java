package com.crpc.interfaces;

import java.util.List;


/**
 * 数据服务
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public interface DataService {

    /**
     * 发送数据
     *
     * @param body 请求体
     * @return {@link String}
     */
    String sendData(String body);

    /**
     * 得到列表
     * 获取数据
     *
     * @return {@link List}<{@link String}>
     */
    List<String> getList();
}
