package com.crpc.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc远程调用包装类
 *
 * @author liuhuaicong
 * @Date created in 11:28 上午 2022/1/29
 */
public class RpcReferenceWrapper<T> {

    private Class<T> aimClass;

    private Map<String,Object> attatchments = new ConcurrentHashMap<>();

    public Class<T> getAimClass() {
        return aimClass;
    }

    public void setAimClass(Class<T> aimClass) {
        this.aimClass = aimClass;
    }

    public boolean isAsync(){
        Object r = attatchments.get("async");
        if (r == null || r.equals(false)) {
            return false;
        }
        return Boolean.TRUE;
    }
    /**
     * 失败重试次数
     */
    public int getRetry(){
        if(attatchments.get("retry")==null){
            return 0;
        }else {
            return (int) attatchments.get("retry");
        }
    }

    public void setRetry(int retry){
        this.attatchments.put("retry",retry);
    }
    public void setAsync(boolean async){
        this.attatchments.put("async",async);
    }

    public String getUrl(){
        return String.valueOf(attatchments.get("url"));
    }

    public void setUrl(String url){
        attatchments.put("url",url);
    }

    public void setTimeOut(int timeOut) {
        attatchments.put("timeOut", timeOut);
    }

    public String getTimeOUt() {
        return String.valueOf(attatchments.get("timeOut"));
    }

    public String getServiceToken(){
        return String.valueOf(attatchments.get("serviceToken"));
    }

    public void setServiceToken(String serviceToken){
        attatchments.put("serviceToken",serviceToken);
    }

    public String getGroup(){
        return String.valueOf(attatchments.get("group"));
    }

    public void setGroup(String group){
        attatchments.put("group",group);
    }

    public Map<String, Object> getAttatchments() {
        return attatchments;
    }

    public void setAttatchments(Map<String, Object> attatchments) {
        this.attatchments = attatchments;
    }
}
