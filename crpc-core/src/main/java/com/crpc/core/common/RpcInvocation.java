package com.crpc.core.common;

import lombok.Data;

import java.util.Arrays;


/**
 * rpc调用
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
@Data
public class RpcInvocation {

    //请求的目标方法，例如findUser
    private String targetMethod;
    //请求的目标服务名称，例如：com.sise.user.UserService
    private String targetServiceName;
    //请求参数信息
    private Object[] args;
    private String uuid;
    //接口响应的数据塞入这个字段中（如果是异步调用或者void类型，这里就为空）
    private Object response;

}
