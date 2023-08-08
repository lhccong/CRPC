package com.crpc.core.server;

import com.crpc.interfaces.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author linhao
 * @Date created in 5:07 下午 2021/12/5
 */
public class DataServiceImpl implements DataService {

    @Override
    public String sendData(String body) {
        System.out.println("己收到的参数长度："+body.length());
        return "success";
    }

    @Override
    public List<String> getList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("idea1");
        arrayList.add("idea2");
        arrayList.add("idea3");
        return arrayList;
    }
}
