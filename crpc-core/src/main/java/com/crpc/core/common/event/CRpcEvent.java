package com.crpc.core.common.event;

/**
 * crpc事件
 *
 * @author liuhuaicong
 * @date 2023/08/11
 */
public interface CRpcEvent {

    Object getData();

    CRpcEvent setData(Object data);
}
