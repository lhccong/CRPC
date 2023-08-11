package com.crpc.core.common.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * crpc侦听器加载程序
 *
 * @author liuhuaicong
 * @date 2023/08/11
 */
public class CRpcListenerLoader {
    private static List<CRpcListener> cRpcListenerList = new ArrayList<>();

    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);

    public static void registerListener(CRpcListener cRpcListener){
        cRpcListenerList.add(cRpcListener);
    }
}
