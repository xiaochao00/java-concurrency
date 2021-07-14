package org.ex.boys.concurrency.nio.ch5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * netty eho client handler
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/14 23:47
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyEchoClientHandler extends ChannelInboundHandlerAdapter {
    public static final NettyEchoClientHandler INSTANCES = new NettyEchoClientHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        int len = buf.readableBytes();
        byte[] bytes = new byte[len];
        buf.getBytes(0, bytes);
        log.info("Client receive:{}.", new String(bytes, StandardCharsets.UTF_8));
        //
        buf.release();
        // 或者向后传
        // super.channelRead(ctx, msg);
    }
}
