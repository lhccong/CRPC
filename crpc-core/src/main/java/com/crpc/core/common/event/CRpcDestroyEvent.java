package com.crpc.core.common.event;

/**
 * 服务销毁事件
 *
 * @author liuhuaicong
 * @date 2023/08/28
 */
public class CRpcDestroyEvent implements CRpcEvent{
    private Object data;

    public CRpcDestroyEvent(Object data) {
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
