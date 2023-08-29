package com.crpc.core.common;

import java.io.Serializable;
import java.util.Arrays;

import static com.crpc.core.common.constants.RpcConstants.MAGIC_NUMBER;


/**
 * rpc协议
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public class RpcProtocol implements Serializable {

    private static final long serialVersionUID = 5359096060555795690L;

    private short magicNumber = MAGIC_NUMBER;
    private int contentLength;
    //这个字段其实是RpcInvocation类的字节数组，在RpcInvocation中包含了更多的调用信息，详情见下方介绍
    private byte[] content;

    public RpcProtocol(byte[] content) {
        this.contentLength = content.length;
        this.content = content;
    }

    public short getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(short magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
