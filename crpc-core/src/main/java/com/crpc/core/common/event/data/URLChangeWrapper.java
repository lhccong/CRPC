package com.crpc.core.common.event.data;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 *
 * @author liuhuaicong
 * @date 2023/08/29
 */
@Data
@ToString
public class URLChangeWrapper {

    private String serviceName;

    private List<String> providerUrl;

}
