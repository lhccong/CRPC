package com.crpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static com.crpc.core.common.constants.RpcConstants.DEFAULT_DECODE_CHAR;


/**
 * rpc编码器
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf out) {
        out.writeShort(msg.getMagicNumber());
        out.writeInt(msg.getContentLength());
        String message = new String(msg.getContent(), StandardCharsets.UTF_8);
        log.info("rpc编码器接收到数据：{}",message);
        out.writeBytes(msg.getContent());
        out.writeBytes(DEFAULT_DECODE_CHAR.getBytes());
    }
}
