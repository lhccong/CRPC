package com.crpc.consumer;


import com.crpc.core.client.Client;
import com.crpc.core.client.ConnectionHandler;
import com.crpc.core.client.RpcReference;
import com.crpc.core.client.RpcReferenceWrapper;
import com.crpc.interfaces.DataService;

/**
 * @Author linhao
 * @Date created in 4:25 下午 2022/2/4
 */
public class ConsumerDemo {

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setGroup("dev");
        rpcReferenceWrapper.setServiceToken("token-a");
        rpcReferenceWrapper.setAsync(false);
        rpcReferenceWrapper.setTimeOut(3000000);
        //在初始化之前必须要设置对应的上下文
        DataService dataService = rpcReference.get(rpcReferenceWrapper);
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();
        for (int i = 0; i < 10000; i++) {
            try {
                String result = dataService.sendData("你好啊我是wanwu创造者");
                System.out.println("没想到吧我收到消息啦"+result);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
