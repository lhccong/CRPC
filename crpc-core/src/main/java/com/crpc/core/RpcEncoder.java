package com.crpc.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;


/**
 * rpc编码器
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf out) throws Exception {
        out.writeShort(msg.getMagicNumber());
        out.writeInt(msg.getContentLength());
        String message = new String(msg.getContent(), StandardCharsets.UTF_8);
        System.out.println("rpc编码器接收到数据："+message);
        out.writeBytes(msg.getContent());
    }
}
