package com.crpc.core.server;


import lombok.Data;

/**
 * 服务包装器
 *
 * @author liuhuaicong
 * @date 2023/10/25
 */
@Data

public class ServiceWrapper {

    /**
     * 对外暴露的具体服务对象
     */
    private Object serviceObj;

    /**
     * 具体暴露服务的分组
     */
    private String group = "default";

    /**
     * 整个应用的token校验
     */
    private String serviceToken = "";

    /**
     * 限流策略
     */
    private Integer limit = -1;
    public ServiceWrapper(Object serviceObj, String group) {
        this.serviceObj = serviceObj;
        this.group = group;
    }


}
