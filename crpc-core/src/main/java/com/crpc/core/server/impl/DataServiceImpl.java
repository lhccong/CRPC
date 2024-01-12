package com.crpc.core.server.impl;

import com.crpc.interfaces.DataService;
import lombok.extern.slf4j.Slf4j;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * 数据服务impl
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
@Slf4j
public class DataServiceImpl implements DataService {
    @Override
    public String sendData(String body) {
        log.info("己收到的参数长度：{}",body.length());
        log.info("己收到的参数：{}",new String(body.getBytes(StandardCharsets.UTF_8)));
        return "我收到你wanwu的数据啦";
    }

    @Override
    public List<String> getList() {
        List<String> arrayList = new ArrayList<>();
        arrayList.add("idea1");
        arrayList.add("idea2");
        arrayList.add("idea3");
        return arrayList;
    }
}
