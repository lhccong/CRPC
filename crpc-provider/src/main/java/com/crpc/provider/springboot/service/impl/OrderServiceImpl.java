package com.crpc.provider.springboot.service.impl;

import com.crpc.interfaces.OrderService;
import com.crpc.starter.common.CRpcService;

import java.util.Arrays;
import java.util.List;

/**
 * 订购服务 IMPL
 *
 * @author cong
 * @date 2024/02/01
 */
@CRpcService(serviceToken = "order-token",group = "order-group",limit = 2)
public class OrderServiceImpl implements OrderService {

    @Override
    public List<String> getOrderNoList() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList("item1","item2");
    }

    //测试大数据包传输是否有异常
    @Override
    public String testMaxData(int i) {
        StringBuffer stb = new StringBuffer();
        for(int j=0;j<i;j++){
            stb.append("1");
        }
        return stb.toString();
    }
}
