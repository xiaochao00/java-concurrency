package org.ex.boys.concurrency.nio.ch5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * netty echo handler
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/13 23:42
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyEchoServerHandler extends ChannelInboundHandlerAdapter {
    public static NettyEchoServerHandler INSTANCE = new NettyEchoServerHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //
        ByteBuf in = (ByteBuf) msg;
        log.info("msg type:{}.", in.hasArray() ? "堆内存" : "直接内存");
        int len = in.readableBytes();
        byte[] arr = new byte[len];
        in.getBytes(0, arr);
        log.info("Server receive msg:{}.", new String(arr, StandardCharsets.UTF_8));
        //
        log.info("写回前，msg.refCnt:{}.", ((ByteBuf) msg).refCnt());
        ChannelFuture f = ctx.writeAndFlush(msg);
        f.addListener((ChannelFutureListener) -> log.info("写回后,msg.refCnt:{}.", ((ByteBuf) msg).refCnt()));
    }
}
