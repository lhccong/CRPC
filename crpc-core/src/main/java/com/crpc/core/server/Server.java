package com.crpc.core.server;

import com.crpc.core.common.RpcDecoder;
import com.crpc.core.common.RpcEncoder;
import com.crpc.core.common.ServerServiceSemaphoreWrapper;
import com.crpc.core.common.annotations.SPI;
import com.crpc.core.common.config.PropertiesBootstrap;
import com.crpc.core.common.config.ServerConfig;
import com.crpc.core.common.event.CRpcListenerLoader;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.filter.ServerFilter;
import com.crpc.core.filter.server.ServerAfterFilterChain;
import com.crpc.core.filter.server.ServerBeforeFilterChain;
import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;
import com.crpc.core.registry.zookeeper.AbstractRegister;
import com.crpc.core.serialize.SerializeFactory;
import com.crpc.core.server.impl.DataServiceImpl;
import com.crpc.core.server.impl.UserServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.crpc.core.common.cache.CommonClientCache.EXTENSION_LOADER;
import static com.crpc.core.common.cache.CommonServerCache.*;
import static com.crpc.core.common.constants.RpcConstants.DEFAULT_DECODE_CHAR;
import static com.crpc.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

/**
 * 服务端
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
@Slf4j
public class Server {

    private ServerConfig serverConfig;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void startApplication() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        //服务端采用单一长连接的模式，这里所支持的最大连接数应该和机器本身的性能有关
        //连接防护的handler应该绑定在Main-Reactor上
        bootstrap.handler(new MaxConnectionLimitHandler(serverConfig.getMaxConnections()));
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                log.info("初始化provider过程");
                ByteBuf delimiter = Unpooled.copiedBuffer(DEFAULT_DECODE_CHAR.getBytes());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(serverConfig.getMaxServerRequestData(), delimiter));
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ServerHandler());
            }
        });
        this.batchExportUrl();
        //开始准备接收请求的任务
        SERVER_CHANNEL_DISPATCHER.startDataConsume();
        bootstrap.bind(serverConfig.getServerPort()).sync();
        IS_STARTED = true;
        log.info("server服务已启动!");
    }

    public void batchExportUrl(){
        Thread task = new Thread(() -> {

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            for (URL url : PROVIDER_URL_SET) {
                REGISTRY_SERVICE.register(url);
            }
        });
        task.start();
    }

    public void initServerConfig() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        this.setServerConfig(serverConfig);
        SERVER_CONFIG = serverConfig;
        //初始化线程池和队列的配置
        SERVER_CHANNEL_DISPATCHER.init(SERVER_CONFIG.getServerQueueSize(),SERVER_CONFIG.getServerBizThreadNums());
        //序列化技术初始化
        String serverSerialize = serverConfig.getServerSerialize();
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        LinkedHashMap<String, Class> serializeFactoryClassMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializeFactoryClass = serializeFactoryClassMap.get(serverSerialize);
        if (serializeFactoryClass == null) {
            log.error("no match serialize type for " + serverSerialize);
            return;
        }
        SERVER_SERIALIZE_FACTORY = (SerializeFactory) serializeFactoryClass.newInstance();

        //过滤链技术初始化
        EXTENSION_LOADER.loadExtension(ServerFilter.class);
        LinkedHashMap<String, Class> iServerFilterClassMap = EXTENSION_LOADER_CLASS_CACHE.get(ServerFilter.class.getName());
        ServerBeforeFilterChain serverBeforeFilterChain = new ServerBeforeFilterChain();
        ServerAfterFilterChain serverAfterFilterChain = new ServerAfterFilterChain();
        for (String iServerFilterKey : iServerFilterClassMap.keySet()) {
            Class iServerFilterClass = iServerFilterClassMap.get(iServerFilterKey);
            if(iServerFilterClass==null){
                log.error("no match iServerFilter type for{} " ,iServerFilterKey);
                return;
            }
            SPI spi = (SPI) iServerFilterClass.getDeclaredAnnotation(SPI.class);
            if (spi != null && "before".equals(spi.value())) {
                serverBeforeFilterChain.addServerFilter((ServerFilter) iServerFilterClass.newInstance());
            } else if(spi != null && "after".equals(spi.value())){
                serverAfterFilterChain.addServerFilter((ServerFilter) iServerFilterClass.newInstance());
            }
        }
        SERVER_AFTER_FILTER_CHAIN = serverAfterFilterChain;
        SERVER_BEFORE_FILTER_CHAIN = serverBeforeFilterChain;
    }

    /**
     * 暴露服务信息
     *
     * @param serviceWrapper 服务Wrapper
     */
    public void exportService(ServiceWrapper serviceWrapper) {
        Object serviceBean = serviceWrapper.getServiceObj();
        if (serviceBean.getClass().getInterfaces().length == 0) {
            log.error("service must had interfaces!");
            return;
        }
        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            log.error("service must only had one interfaces!");
            return;
        }
        if (REGISTRY_SERVICE == null) {
            try {
                EXTENSION_LOADER.loadExtension(RegistryService.class);
                Map<String, Class> registryClassMap = EXTENSION_LOADER_CLASS_CACHE.get(RegistryService.class.getName());
                Class registryClass = registryClassMap.get(serverConfig.getRegisterType());
                REGISTRY_SERVICE = (AbstractRegister) registryClass.newInstance();
            } catch (Exception e) {
                log.error("registryServiceType unKnow,error is ", e);
            }
        }
        //默认选择该对象的第一个实现接口
        Class interfaceClass = classes[0];
        //需要注册的对象统一放在一个MAP集合中进行管理
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setApplicationName(serverConfig.getApplicationName());
        url.addParameter("host", CommonUtils.getIpAddress());
        url.addParameter("port", String.valueOf(serverConfig.getServerPort()));
        url.addParameter("group", String.valueOf(serviceWrapper.getGroup()));
        url.addParameter("limit", String.valueOf(serviceWrapper.getLimit()));
        //设置服务端的限流器
        SERVER_SERVICE_SEMAPHORE_MAP.put(interfaceClass.getName(),new ServerServiceSemaphoreWrapper(serviceWrapper.getLimit()));
        PROVIDER_URL_SET.add(url);
        if (!CommonUtils.isEmpty(serviceWrapper.getServiceToken())) {
            PROVIDER_SERVICE_WRAPPER_MAP.put(interfaceClass.getName(), serviceWrapper);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        CRpcListenerLoader cRpcListenerLoader;
        Server server = new Server();
        server.initServerConfig();
        cRpcListenerLoader = new CRpcListenerLoader();
        cRpcListenerLoader.init();
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
