package com.crpc.core.server;

import com.crpc.core.RpcDecoder;
import com.crpc.core.RpcEncoder;
import com.crpc.core.common.config.PropertiesBootstrap;
import com.crpc.core.common.config.ServerConfig;
import com.crpc.core.common.utils.CommonUtils;
import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import sun.applet.AppletIllegalArgumentException;


import static com.crpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;
import static com.crpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;

/**
 * 服务端
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public class Server {

    private ServerConfig serverConfig;
    private RegistryService registryService;

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

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                System.out.println("初始化provider过程");
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ServerHandler());
            }
        });
        this.batchExportUrl();
        bootstrap.bind(serverConfig.getServerPort()).sync();
    }

    public void batchExportUrl(){
        Thread task = new Thread(() -> {

            try {
                Thread.sleep(2500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (URL url : PROVIDER_URL_SET) {
                registryService.register(url);
            }
        });
        task.start();
    }

    public void initServerConfig() {
        this.serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        this.setServerConfig(serverConfig);
    }

    /**
     * 暴露服务信息
     *
     * @param serviceBean 服务bean
     */
    public void exportService(Object serviceBean) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new AppletIllegalArgumentException("service must had interfaces!");
        }
        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new AppletIllegalArgumentException("service must only had one interfaces!");
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
        PROVIDER_URL_SET.add(url);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.initServerConfig();
        server.exportService(new DataServiceImpl());
        server.startApplication();
    }

}
