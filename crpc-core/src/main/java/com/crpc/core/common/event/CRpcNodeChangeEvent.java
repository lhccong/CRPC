package com.crpc.core.common.event;

/**
 * crpc节点更改事件
 *
 * @author liuhuaicong
 * @date 2023/08/28
 */
public class CRpcNodeChangeEvent implements CRpcEvent{
    private Object data;

    public CRpcNodeChangeEvent(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public CRpcNodeChangeEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
