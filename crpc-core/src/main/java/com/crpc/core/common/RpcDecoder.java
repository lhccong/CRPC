package com.crpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.crpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * rpc解码器
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * 协议的开头部分的标准长度
     */
    public final int BASE_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        if (byteBuf.readableBytes() >= BASE_LENGTH) {
            //防止收到一些体积过大的数据包 目前限制在1000大小，后期版本这里是可配置模式
            if (byteBuf.readableBytes() > 1000) {
                byteBuf.skipBytes(byteBuf.readableBytes());
            }
            int beginReader;

            beginReader = byteBuf.readerIndex();
            byteBuf.markReaderIndex();
            if (byteBuf.readShort() != MAGIC_NUMBER) {
                // 不是魔数开头，说明是非法的客户端发来的数据包
                ctx.close();
                return;
            }

            int length = byteBuf.readInt();
            //说明剩余的数据包不是完整的，这里需要重置下读索引
            if (byteBuf.readableBytes() < length) {
                byteBuf.readerIndex(beginReader);
                return;
            }
            byte[] data = new byte[length];

            byteBuf.readBytes(data);
            String message = new String(data, StandardCharsets.UTF_8);
            log.info("rpc解码器接收到数据{}",message);
            RpcProtocol rpcProtocol = new RpcProtocol(data);

            out.add(rpcProtocol);
        }
    }
}
