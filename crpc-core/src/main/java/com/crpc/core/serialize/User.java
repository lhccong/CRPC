package com.crpc.core.serialize;

import lombok.Data;

import java.io.Serializable;


/**
 * 测试自定义序列化技术时使用的demo
 * @author liuhuaicong
 * @date 2023/09/18
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = -1728196331321496561L;

    private Integer id;

    private Long tel;

}
