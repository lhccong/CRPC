package com.crpc.provider;

import com.crpc.core.common.event.CRpcListenerLoader;
import com.crpc.core.server.ApplicationShutdownHook;
import com.crpc.core.server.Server;
import com.crpc.core.server.ServiceWrapper;
import com.crpc.core.server.impl.DataServiceImpl;
import com.crpc.core.server.impl.UserServiceImpl;

import java.io.IOException;

/**
 * 提供程序演示
 *
 * @author cong
 * @date 2024/01/12
 */
public class ProviderDemo {

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        Server server = new Server();
        server.initServerConfig();
        CRpcListenerLoader iRpcListenerLoader = new CRpcListenerLoader();
        iRpcListenerLoader.init();
        ServiceWrapper dataServiceServiceWrapper = new ServiceWrapper(new DataServiceImpl(), "dev");
        dataServiceServiceWrapper.setServiceToken("token-a");
        dataServiceServiceWrapper.setLimit(2);
        ServiceWrapper userServiceServiceWrapper = new ServiceWrapper(new UserServiceImpl(), "dev");
        userServiceServiceWrapper.setServiceToken("token-b");
        userServiceServiceWrapper.setLimit(2);
        server.exportService(dataServiceServiceWrapper);
        server.exportService(userServiceServiceWrapper);
        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
    }
}
