package com.crpc.core.common.event;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author linhao
 * @Date created in 8:56 下午 2021/12/19
 */
@Data
@ToString
public class URLChangeWrapper {

    private String serviceName;

    private List<String> providerUrl;

}
