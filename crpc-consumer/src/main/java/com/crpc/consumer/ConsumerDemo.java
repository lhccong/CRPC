package com.crpc.consumer;


import com.crpc.core.client.Client;
import com.crpc.core.client.ConnectionHandler;
import com.crpc.core.client.RpcReference;
import com.crpc.core.client.RpcReferenceWrapper;
import com.crpc.interfaces.DataService;
import lombok.extern.slf4j.Slf4j;


/**
 * 消费者演示
 *
 * @author cong
 * @date 2024/01/12
 */
@Slf4j
public class ConsumerDemo {

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setGroup("dev");
        rpcReferenceWrapper.setServiceToken("token-a");
        //在初始化之前必须要设置对应的上下文
        DataService dataService = rpcReference.get(rpcReferenceWrapper);
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();
        for (int i = 0; i < 10000; i++) {
            try {
                String result = dataService.sendData("test");
                System.out.println(result);
                Thread.sleep(1000);
            } catch (Exception e) {
               log.error(e.getMessage());
            }
        }
    }
}
