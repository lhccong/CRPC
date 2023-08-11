package com.crpc.core.common.event;

/**
 * crpc更新事件
 *
 * @author liuhuaicong
 * @date 2023/08/11
 */
public class CRpcUpdateEvent implements CRpcEvent{

    private Object data;

    public CRpcUpdateEvent(Object data) {
        this.data = data;
    }
    @Override
    public Object getData() {
        return data;
    }

    @Override
    public CRpcEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
