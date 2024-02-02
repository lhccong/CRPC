# CRPC一款轻量级的RPC框架🎄

## 项目介绍🌸

一款基于`Netty`+`Zookeeper`+`Spring`实现的轻量级`Java RPC`框架。提供服务注册，发现，负载均衡，支持`API`调用，`Spring`集成和`Spring Boot starter`使用。是一个学习`RPC`工作原理的良好示例。

通过这个简易项目的学习，可以让你从零开始实现一个类似` Dubbo` 服务框架 mini 版`RPC`，学到` RPC` 的底层原理以及各种 `Java` 编码实践的运用。下面看一下`RPC`的调用流程：

<img src="https://shaogezhu.cn/assets/2022-11/rpc2.png" style="zoom:66%;" />



## 功能&设计🚀

### 目录结构

```txt
crpc框架
├─crpc-core	--rpc核心实现类
├─crpc-spring-starter	--组件的spring-starter接入类
├─rpc-consumer	--[示例]服务消费者
├─rpc-interface	--存放服务接口
└─rpc-provider	--[示例]服务提供者
```

### 核心模块结构

```
├── cache                          -> 服务端以及客户端缓存
├── client                         -> 客户端相关类（请求处理、启动加载）
├── common                         -> 通用模块
├── config                         -> 项目配置（服务端、客户端属性配置）
├── constants                      -> 项目常量
├── dispatcher                     -> 服务端请求解耦
├── event                          -> 事件监听机制
├── exception                      -> 全局异常
├── filter                         -> 责任链模式过滤请求
├── proxy                          -> 动态代理
├── registry                       -> 注册中心
├── router                         -> 路由选择负载均衡
├── serialize                      -> 序列化与反序列化
├── server                         -> 服务端相关类（请求处理、启动加载）
├── service                        -> 测试服务接口
├── spi                            -> SPI自定义加载类
└── utils                          -> 项目工具包
```

### 功能：

- 简单易学的代码和框架，**在代码中含有大量注解**
- 基于`Netty`实现长连接通信，包括心跳检测、解决粘包半包等
- 基于`Zookeeper`实现分布式服务注册与发现
- 实现了轮询、随机、加权随机等负载均衡算法
- 实现了同步调用、异步调用多种调用方式
- 支持`jdk`的动态代理方式
- 支持`fastJson`、`hessian`、`kryo`、`jdk`的序列化方式
- 支持简易扩展点，泛化调用等功能
- 加入了`Spring Boot Starter`



### 设计：

**`crpc`框架调用流程：**

![](https://markdown-piggo.oss-cn-guangzhou.aliyuncs.com/img/image-20230725143110454.png#id=IV1CZ&originHeight=719&originWidth=1440&originalType=binary&ratio=1&rotation=0&showTitle=false&status=done&style=none&title=)



- **代理层**：负责对底层调用细节的封装；
- **链路层**：负责执行一些自定义的过滤链路，可以供后期二次扩展；
- **路由层**：负责在集群目标服务中的调用筛选策略；
- **协议层**：负责请求数据的转码封装等作用；
- **注册中心**：关注服务的上下线，以及一些权重，配置动态调整等功能；
- **容错层**：当服务调用出现失败之后需要有容错层的兜底辅助；




## 快速开始🌈

### 环境准备

- JDK8 或以上
- Maven 3
- Zookeeper 单机或者集群实例



### 启动示例

**方式一**：使用本项目中的测试用例

1. 将项目克隆到本地

   ```shell
   git clone https://github.com/lhccong/CRPC.git
   ```

2. IDEA打开项目

   使用 IDEA 打开，等待项目初始化完成。

3. 运行`Zookeeper

   如果没有安装的过需要先去下载。**（本地使用可参考最后FQA）**

4. 修改配置文件

   修改客户端和服务端`crpc.properties`配置文件中zookeeper的地址(配置文件中位默的地址为`localhost:2181`)

5. 启动项目（按照图中顺序）

   PS：启动项目前，要确保`zookeeper`已启动.

	![image-20240202090654817](https://markdown-piggo.oss-cn-guangzhou.aliyuncs.com/img/image-20240202090654817.png)

6. 打开浏览器测试

   在浏览器中输入`http://localhost:8019/user/test`或者`http://localhost:8019/user/list`，然后查看项目的输出日志。



**方式二**：将该`rpc`框架运用到自己项目中

1. 下载源码

   ```shell
   git clone https://github.com/lhccong/CRPC.git
   ```

2. 编译安装 jar 包到本地仓库（注意如果是服务器上面，需要上传到私服仓库）

   ```shell
   mvn clean install
   ```

3. 新建`Spring Boot Maven`工程

   在本地新建两个工程，用于模拟客户端和服务端。

	![image-20240202090913855](https://markdown-piggo.oss-cn-guangzhou.aliyuncs.com/img/image-20240202090913855.png)



4. 引入入依赖

   在项目中的`pom`引入刚刚安装的依赖（客户端、服务端都需要引入）
   ```xml
   <dependency>
    	<groupId>com.cong</groupId>
       <artifactId>crpc-spring-starter</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
   <dependency>
       <groupId>com.cong</groupId>
       <artifactId>crpc-core</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
   ```

5. 定义服务接口

   ```java
   public interface DataService {
   
       /**
        * 发送数据
        * @param msg 内容
        * @return 服务端消息
        */
       String sendData(String msg);
   }
   ```
   
6. 实现接口，使用自定义注解`@CRpcService` 暴露一个服务接口

   ```java
   @CRpcService
   public class DataServiceImpl implements DataService {
   
       @Override
       public String sendData(String body) {
           System.out.println("这里是服务提供者，body is " + body);
           return "success from server";
       }
   }
   ```
   
7. 服务端配置

   在服务端模块的`resource`文件夹下新建`crpc.properties`文件，并加入以下配置

   ```properties
   #服务端对外暴露的端口
   crpc.serverPort=9093
   #注册中心（zookeeper）的地址
   crpc.registerAddr=localhost:2181
   #项目名称
   crpc.applicationName=crpc-provider
   #代理类型
   crpc.proxyType=jdk
   #路由策略（负载均衡）
   crpc.routerStrategy=rotate
   #序列化类型
   crpc.serverSerialize=fastJson
   #注册中心类型
   crpc.registerType=zookeeper
   #队列大小
   crpc.server.queue.size=513
   #线程数
   crpc.server.biz.thread.nums=257
   #最大连接数
   crpc.server.max.connection=100
   #数据包最大大小
   crpc.server.max.data.size=4096
   ```

8. 使用自定义注解 `@CRpcReference` 自动注入服务端暴露的接口服务

   ```java
   @RestController
   @RequestMapping(value = "/data")
   public class DataController {
   
       @CRpcReference
       private DataService dataService;
   
       @GetMapping(value = "/send/{msg}")
       public String sendMsg(@PathVariable(name = "msg") String msg){
           return dataService.sendData(msg);
       }
   }
   ```

9. 客户端配置

   在客户端模块的`resource`文件夹下新建`rpc.properties`文件，并加入以下配置

   ```properties
   #项目名称
   rpc.applicationName=crpc-consumer
   #注册中心（zookeeper）的地址
   rpc.registerAddr=127.0.0.1:2181
   #注册中心类型
   rpc.registerType=zookeeper
   #代理方式（jdk、javassist）
   rpc.proxyType=jdk
   #路由策略（负载均衡）
   rpc.router=rotate
   #客户端序列化方式
   rpc.clientSerialize=fastJson
   #数据包最大大小
   crpc.server.max.data.size=4096
   ```

10. 启动项目

    首先启动服务端（服务提供者），再启动客户端（服务消费者）。

11. 测试

    打开浏览器，输入`http://localhost:8081/user/send/helloworld`。有字符串返回就说明运行成功。



## FAQ

**1、`zookeeper` 连接失败**

![](https://shaogezhu-images.oss-cn-beijing.aliyuncs.com/my/linkerror.png)

解决方法：

（1）在本地机器或者在服务器上安装运行 `zookeeper` 实例；

​			Zookeeper官网连接地址：https://www.apache.org/dyn/closer.cgi/zookeeper/

（2）在配置文件中正确配置 `zookeeper` 地址；

**（3）windows部署zookeeper**

Zookeeper官网连接地址：https://www.apache.org/dyn/closer.cgi/zookeeper/

![img](https://cdn.nlark.com/yuque/0/2023/png/22423603/1692599761592-f4db6e55-193e-4757-b64d-451ca25201d6.png)

![img](https://cdn.nlark.com/yuque/0/2023/png/22423603/1692599777628-3e8f32bb-7033-47c9-b466-0552c75e527e.png)

我们发现zookeeper是闪退。解决这个问题，需要我们修改zkServer.cmd配置内容，用文本打开，在文件末尾添加pause即可，如下：

![img](https://cdn.nlark.com/yuque/0/2023/png/22423603/1692599820618-85cbca06-b061-4a9a-9b94-17a82d197464.png)



