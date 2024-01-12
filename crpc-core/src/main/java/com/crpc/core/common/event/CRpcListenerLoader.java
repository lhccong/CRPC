package com.crpc.core.common.event;

import com.crpc.core.common.event.listener.CRpcListener;
import com.crpc.core.common.event.listener.ProviderNodeDataChangeListener;
import com.crpc.core.common.event.listener.ServiceDestroyListener;
import com.crpc.core.common.event.listener.ServiceUpdateListener;
import com.crpc.core.common.utils.CommonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
    private static final List<CRpcListener> C_RPC_LISTENER_LIST = new ArrayList<>();

    private static final ExecutorService EVENT_THREAD_POOL = Executors.newFixedThreadPool(2);

    public static void registerListener(CRpcListener cRpcListener) {
        C_RPC_LISTENER_LIST.add(cRpcListener);
    }

    public void init() {
        registerListener(new ServiceUpdateListener());

        registerListener(new ServiceDestroyListener());

        registerListener(new ProviderNodeDataChangeListener());
    }

    /**
     * 获取接口上的泛型
     *
     * @param o 接口
     * @return {@link Class}<{@link ?}>
     */
    public static Class<?> getInterfaceT(Object o) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * 发送同步事件
     *
     * @param cRpcEvent c rpc事件
     */
    public static void sendSyncEvent(CRpcEvent cRpcEvent){
        if (CommonUtils.isEmptyList(C_RPC_LISTENER_LIST)) {
            return;
        }
        for (CRpcListener<?> cRpcListener : C_RPC_LISTENER_LIST) {
            Class<?> type = getInterfaceT(cRpcListener);
            assert type != null;
            if (type.equals(cRpcEvent.getClass())) {
                cRpcListener.callBack(cRpcEvent.getData());
            }
        }
    }

    public static void sendEvent(CRpcEvent cRpcEvent) {
        if (CommonUtils.isEmptyList(C_RPC_LISTENER_LIST)) {
            return;
        }
        for (CRpcListener<?> cRpcListener : C_RPC_LISTENER_LIST) {
            Class<?> type = getInterfaceT(cRpcListener);
            assert type != null;
            if (type.equals(cRpcEvent.getClass())) {
                EVENT_THREAD_POOL.execute(() -> cRpcListener.callBack(cRpcEvent.getData()));
            }
        }
    }

}
