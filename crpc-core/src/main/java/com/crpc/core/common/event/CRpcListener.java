package com.crpc.core.common.event;

/**
 * crpc侦听器
 *
 * @author liuhuaicong
 * @date 2023/08/11
 */
public interface CRpcListener<T> {

    void callBack(Object t);
}
