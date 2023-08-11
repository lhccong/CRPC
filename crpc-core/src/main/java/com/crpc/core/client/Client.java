package com.crpc.core.client;

import com.alibaba.fastjson.JSON;
import com.crpc.core.RpcDecoder;
import com.crpc.core.RpcEncoder;
import com.crpc.core.RpcInvocation;
import com.crpc.core.RpcProtocol;
import com.crpc.core.common.config.ClientConfig;
import com.crpc.core.proxy.jdk.JDKProxyFactory;
import com.crpc.interfaces.DataService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


import static com.crpc.core.common.cache.CommonClientCache.SEND_QUEUE;


/**
 * 客户端
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */

public class Client {

    private static final EventLoopGroup CLIENT_GROUP = new NioEventLoopGroup();

    private ClientConfig clientConfig;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

 public RpcReference startClientApplication() throws InterruptedException {
     Bootstrap bootstrap = new Bootstrap();
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
     //常规的连接netty服务端
     ChannelFuture channelFuture = bootstrap.connect(clientConfig.getServerAddr(), clientConfig.getPort()).sync();
     System.out.println("============ 服务启动 ============");
     this.startClient(channelFuture);
     //注入代理工厂
     return new RpcReference(new JDKProxyFactory());
 }

    /**
     * 开启发送线程，专门从事将数据包发送给服务端，起到一个解耦的效果
     */
    private void startClient(ChannelFuture channelFuture) {
        Thread asyncSendJob = new Thread(new AsyncSendJob(channelFuture));
        asyncSendJob.start();
    }
    /**
     * 异步发送信息任务
     *
     */
    static class AsyncSendJob implements Runnable {

        private final ChannelFuture channelFuture;

        public AsyncSendJob(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //阻塞模式
                    RpcInvocation data = SEND_QUEUE.take();
                    //将RpcInvocation封装到RpcProtocol对象中，然后发送给服务端，这里正好对应了上文中的ServerHandler
                    String json = JSON.toJSONString(data);
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());

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
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setPort(9090);
        clientConfig.setServerAddr("localhost");
        client.setClientConfig(clientConfig);
        RpcReference rpcReference = client.startClientApplication();
        DataService dataService = rpcReference.get(DataService.class);
        String result = dataService.sendData("test");
        System.out.println(result);

    }
}
