package com.crpc.core.server;


import com.crpc.core.common.event.CRpcDestroyEvent;
import com.crpc.core.common.event.CRpcListenerLoader;
import lombok.extern.slf4j.Slf4j;


/**
 * 监听java进程被关闭
 *
 * @author liuhuaicong
 * @date 2023/08/28
 */
@Slf4j
public class ApplicationShutdownHook {
    private ApplicationShutdownHook() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 注册一个shutdownHook的钩子，当jvm进程关闭的时候触发
     */
    public static void registryShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[registryShutdownHook] ==== ");
            CRpcListenerLoader.sendSyncEvent(new CRpcDestroyEvent("destroy"));
            log.info("destroy");
        }));
    }

}
