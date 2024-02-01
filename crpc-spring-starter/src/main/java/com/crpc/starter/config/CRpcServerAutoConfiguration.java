package com.crpc.starter.config;

import com.crpc.core.common.event.CRpcListenerLoader;
import com.crpc.core.server.ApplicationShutdownHook;
import com.crpc.core.server.Server;
import com.crpc.core.server.ServiceWrapper;
import com.crpc.starter.common.CRpcService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * CRPC服务器自动配置
 *
 * @author cong
 * @date 2024/02/01
 */
@Slf4j
public class CRpcServerAutoConfiguration implements InitializingBean, ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Server server = null;
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(CRpcService.class);
        if (beanMap.size() == 0) {
            //说明当前应用内部不需要对外暴露服务
            return;
        }
        printBanner();
        long begin = System.currentTimeMillis();
        server = new Server();
        server.initServerConfig();
        CRpcListenerLoader cpcListenerLoader = new CRpcListenerLoader();
        cpcListenerLoader.init();
        for (String beanName : beanMap.keySet()) {
            Object bean = beanMap.get(beanName);
            CRpcService cRpcService = bean.getClass().getAnnotation(CRpcService.class);
            ServiceWrapper dataServiceServiceWrapper = new ServiceWrapper(bean, cRpcService.group());
            dataServiceServiceWrapper.setServiceToken(cRpcService.serviceToken());
            dataServiceServiceWrapper.setLimit(cRpcService.limit());
            server.exportService(dataServiceServiceWrapper);
            log.info(">>>>>>>>>>>>>>> [crpc] {} export success! >>>>>>>>>>>>>>> ",beanName);
        }
        long end = System.currentTimeMillis();
        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
        log.info(" ================== [{}] started success in {}s ================== ",server.getServerConfig().getApplicationName(),((double)end-(double)begin)/1000);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void printBanner(){
        System.out.println();
        System.out.println("==============================================");
        System.out.println("|||---------- CRpc Starting Now! ----------|||");
        System.out.println("==============================================");
        System.out.println("源代码地址: https://github.com/lhccong/CRPC");
        System.out.println("version: 1.0.0");
        System.out.println();
    }
}
