package com.crpc.core.client;

import com.alibaba.fastjson.JSON;
import com.crpc.core.RpcDecoder;
import com.crpc.core.RpcEncoder;
import com.crpc.core.RpcInvocation;
import com.crpc.core.RpcProtocol;
import com.crpc.core.common.config.ClientConfig;
import com.crpc.core.common.config.PropertiesBootstrap;
import com.crpc.core.common.event.CRpcListenerLoader;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.proxy.jdk.JDKProxyFactory;
import com.crpc.core.registry.URL;
import com.crpc.core.registry.zookeeper.AbstractRegister;
import com.crpc.core.registry.zookeeper.ZookeeperRegister;
import com.crpc.interfaces.DataService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


import java.util.List;

import static com.crpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static com.crpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;


/**
 * 客户端
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
@Slf4j
public class Client {
    private static final EventLoopGroup CLIENT_GROUP = new NioEventLoopGroup();

    private ClientConfig clientConfig;

    private CRpcListenerLoader cRpcListenerLoader;
    private Bootstrap bootstrap;

    private AbstractRegister abstractRegister;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
    public Bootstrap getBootstrap() {
        return bootstrap;
    }
    public RpcReference initClientApplication() {
        bootstrap = new Bootstrap();
        bootstrap.group(CLIENT_GROUP);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //管道中初始化一些逻辑，这里包含了上边所说的编解码器和客户端响应类
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });
        cRpcListenerLoader = new CRpcListenerLoader();
        cRpcListenerLoader.init();
        this.clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();
        return new RpcReference(new JDKProxyFactory());
    }

    /**
     * 启动服务之前需要预先订阅对应的dubbo服务
     *
     * @param serviceBean 服务bean
     */
    public void doSubscribeService(Class serviceBean) {
        if (abstractRegister == null) {
            abstractRegister = new ZookeeperRegister(clientConfig.getRegisterAddr());
        }
        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParameter("host", CommonUtils.getIpAddress());
        abstractRegister.subscribe(url);

    }


    public void doConnectServer() {
        for (String providerServiceName : SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = abstractRegister.getProviderIps(providerServiceName);
            for (String providerIp : providerIps) {
                try {
                    ConnectionHandler.connect(providerServiceName, providerIp);
                } catch (InterruptedException e) {
                    log.error("[doConnectServer] connect fail ", e);
                    Thread.currentThread().interrupt();
                }
            }
            URL url = new URL();
            url.setServiceName(providerServiceName);
            abstractRegister.doAfterSubscribe(url);
        }
    }

    /**
     * 开启发送线程，专门从事将数据包发送给服务端，起到一个解耦的效果
     */
    private void startClient() {
        Thread asyncSendJob = new Thread(new AsyncSendJob());
        asyncSendJob.start();
    }

    /**
     * 异步发送信息任务
     */
    static class AsyncSendJob implements Runnable {

        Boolean isTarget = false;


        @Override
        public void run() {
            while (true) {
                try {
                    if (Boolean.TRUE.equals(isTarget)) {
                        break;
                    }
                    //阻塞模式
                    RpcInvocation data = SEND_QUEUE.take();
                    //将RpcInvocation封装到RpcProtocol对象中，然后发送给服务端，这里正好对应了上文中的ServerHandler
                    String json = JSON.toJSONString(data);
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(data.getTargetServiceName());
                    //netty的通道负责发送数据给服务端
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {

        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        DataService dataService = rpcReference.get(DataService.class);
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();
        String result = dataService.sendData("test");
        log.info(result);

    }
}
