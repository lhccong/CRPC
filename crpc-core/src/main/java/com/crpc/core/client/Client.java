package com.crpc.core.client;

import com.alibaba.fastjson.JSON;
import com.crpc.core.common.RpcDecoder;
import com.crpc.core.common.RpcEncoder;
import com.crpc.core.common.RpcInvocation;
import com.crpc.core.common.RpcProtocol;
import com.crpc.core.common.config.ClientConfig;
import com.crpc.core.common.config.PropertiesBootstrap;
import com.crpc.core.common.event.CRpcListenerLoader;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.filter.ClientFilter;
import com.crpc.core.filter.client.ClientFilterChain;
import com.crpc.core.proxy.ProxyFactory;
import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;
import com.crpc.core.registry.zookeeper.AbstractRegister;
import com.crpc.core.router.CRouter;
import com.crpc.core.serialize.SerializeFactory;
import com.crpc.interfaces.DataService;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.crpc.core.common.cache.CommonClientCache.*;
import static com.crpc.core.common.constants.RpcConstants.DEFAULT_DECODE_CHAR;
import static com.crpc.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;


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

    private Bootstrap bootstrap;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
    public Bootstrap getBootstrap() {
        return bootstrap;
    }
    public RpcReference initClientApplication() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        bootstrap = new Bootstrap();
        bootstrap.group(CLIENT_GROUP);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //管道中初始化一些逻辑，这里包含了上边所说的编解码器和客户端响应类
                ByteBuf delimiter = Unpooled.copiedBuffer(DEFAULT_DECODE_CHAR.getBytes());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(clientConfig.getMaxServerRespDataSize(), delimiter));
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });
        CRpcListenerLoader cRpcListenerLoader = new CRpcListenerLoader();
        cRpcListenerLoader.init();
        this.clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();
        CLIENT_CONFIG = this.clientConfig;
        //spi扩展的加载部分
        this.initClientConfig();
        EXTENSION_LOADER.loadExtension(ProxyFactory.class);
        String proxyType = clientConfig.getProxyType();
        LinkedHashMap<String, Class> classMap = EXTENSION_LOADER_CLASS_CACHE.get(ProxyFactory.class.getName());
        Class proxyClassType = classMap.get(proxyType);
        ProxyFactory proxyFactory = (ProxyFactory) proxyClassType.newInstance();
        return new RpcReference(proxyFactory);
    }

    /**
     * 启动服务之前需要预先订阅对应的dubbo服务
     *
     * @param serviceBean 服务bean
     */
    public void doSubscribeService(Class serviceBean) {
        if (ABSTRACT_REGISTER == null) {
            try {
                EXTENSION_LOADER.loadExtension(RegistryService.class);
                Map<String, Class> registerMap = EXTENSION_LOADER_CLASS_CACHE.get(RegistryService.class.getName());
                Class registerClass =  registerMap.get(clientConfig.getRegisterType());
                ABSTRACT_REGISTER = (AbstractRegister) registerClass.newInstance();
            } catch (Exception e) {
                log.error("registryServiceType unKnow,error is {}",e.getMessage());
            }
        }
        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParameter("host", CommonUtils.getIpAddress());
        Map<String, String> result = ABSTRACT_REGISTER.getServiceWeightMap(serviceBean.getName());
        URL_MAP.put(serviceBean.getName(),result);
        ABSTRACT_REGISTER.subscribe(url);

    }


    /**
     * 开始和各个provider建立连接
     */

    public void doConnectServer() {
        for (URL providerUrl : SUBSCRIBE_SERVICE_LIST) {
            //从注册中心获取服务端的地址
            List<String> providerIps = ABSTRACT_REGISTER.getProviderIps(providerUrl.getServiceName());
            for (String providerIp : providerIps) {
                try {
                    ConnectionHandler.connect(providerUrl.getServiceName(), providerIp);
                } catch (InterruptedException e) {
                    log.error("[doConnectServer] connect fail ", e);
                    Thread.currentThread().interrupt();
                }
            }
            URL url = new URL();
            url.addParameter("servicePath",providerUrl.getServiceName() + "/provider");
            url.addParameter("providerIps",JSON.toJSONString(providerIps));
            ABSTRACT_REGISTER.doAfterSubscribe(url);
        }
    }

    /**
     * 开启发送线程，专门从事将数据包发送给服务端，起到一个解耦的效果
     */
    public void startClient() {
        Thread asyncSendJob = new Thread(new AsyncSendJob());
        asyncSendJob.start();
    }

    /**
     * 异步发送信息任务
     */
     class AsyncSendJob implements Runnable {

        Boolean isTarget = false;


        @Override
        public void run() {
            while (true) {
                try {
                    if (Boolean.TRUE.equals(isTarget)) {
                        break;
                    }
                    //阻塞模式
                    RpcInvocation rpcInvocation = SEND_QUEUE.take();
                    //将RpcInvocation封装到RpcProtocol对象中，然后发送给服务端，这里正好对应了上文中的ServerHandler
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(rpcInvocation);
                    //netty的通道负责发送数据给服务端
                    if (channelFuture != null) {
                        RpcProtocol rpcProtocol = new RpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(rpcInvocation));
                        channelFuture.channel().writeAndFlush(rpcProtocol);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 初始化客户端配置
     * 加入spi
     */
    private void initClientConfig() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        //初始化路由策略
        EXTENSION_LOADER.loadExtension(CRouter.class);
        String routerStrategy = clientConfig.getRouterStrategy();
        LinkedHashMap<String, Class> iRouterMap = EXTENSION_LOADER_CLASS_CACHE.get(CRouter.class.getName());
        Class iRouterClass = iRouterMap.get(routerStrategy);
        if (iRouterClass == null) {
            log.error("no match routerStrategy for " + routerStrategy);
            return;
        }
        CROUTER = (CRouter) iRouterClass.newInstance();

        //初始化序列化框架
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        String clientSerialize = clientConfig.getClientSerialize();
        LinkedHashMap<String, Class> serializeMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializeFactoryClass = serializeMap.get(clientSerialize);
        if (serializeFactoryClass == null) {
            log.error("no match serialize type for null ");
            return;
        }
        CLIENT_SERIALIZE_FACTORY = (SerializeFactory) serializeFactoryClass.newInstance();


        //初始化过滤链
        EXTENSION_LOADER.loadExtension(ClientFilter.class);
        ClientFilterChain clientFilterChain = new ClientFilterChain();
        LinkedHashMap<String, Class> iClientMap = EXTENSION_LOADER_CLASS_CACHE.get(ClientFilter.class.getName());
        for (String implClassName : iClientMap.keySet()) {
            Class iClientFilterClass = iClientMap.get(implClassName);
            if (iClientFilterClass == null) {
                log.error("no match iClientFilter for null ");
                return;
            }
            clientFilterChain.addClientFilter((ClientFilter) iClientFilterClass.newInstance());
        }
        CLIENT_FILTER_CHAIN = clientFilterChain;
    }
    public static void main(String[] args) throws Throwable {

        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        client.initClientConfig();
        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setGroup("dev");
        rpcReferenceWrapper.setServiceToken("token-a");
        //获取代理对象，设置缓存信息，订阅时调用

        DataService dataService = rpcReference.get(rpcReferenceWrapper);
        //订阅某个服务，添加本地缓存SUBSCRIBE_SERVICE_LIST
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        //订阅服务，从SUBSCRIBE_SERVICE_LIST中获取需要订阅的服务信息，添加注册中心的监听
        //根据服务生产者信息，建立连接ChannelFuture,建立的ChannelFuture放入CONNECT_MAP
        client.doConnectServer();
        //开启异步线程，发送函数请求，通过队列SEND_QUEUE进行通信
        client.startClient();
        //被代理层invoke方法，增强功能（拦截），将请求放入队列SEND_QUEUE中
        //异步线程asyncSendJob接收到SEND_QUEUE数据，发起netty调用；在invoke方法中3*1000时间内"死循环获取"RESP_MAP缓存中的响应数据
        //在ClientHandler中将请求方法，将响应数据放入RESP_MAP中
        String result = dataService.sendData("test");
        log.info(result);

    }
}
