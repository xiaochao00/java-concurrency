package org.ex.boys.concurrency.nio.ch5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.ex.boys.concurrency.nio.NioConfig;

/**
 * netty echo server
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/11 20:47
 */
@Slf4j
public class NettyEchoServer {
    public static void main(String[] args) {
        new NettyEchoServer().runServer();
    }

    public void runServer() {
        // boot strap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 创建反应器轮询组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 设置反应器轮询组
        serverBootstrap.group(bossGroup, workerGroup);
        // 设置NIO类型的通道
        serverBootstrap.channel(NioServerSocketChannel.class);
        // 设置监听端口
        serverBootstrap.localAddress(NioConfig.SOCKET_SERVER_PORT);
        // 设置通道的参数
        serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        // 装配子通道流水线
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(NettyEchoServerHandler.INSTANCE);
            }
        });
        try {
            // 开始绑定服务端
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            log.info("Server start success.");
            // 等待通道关闭的异步任务结束
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅关闭
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
