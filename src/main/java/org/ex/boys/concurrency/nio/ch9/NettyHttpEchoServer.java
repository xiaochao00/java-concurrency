package org.ex.boys.concurrency.nio.ch9;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;
import org.ex.boys.concurrency.nio.NioConfig;
import org.ex.boys.concurrency.nio.ch5.NettyEchoServer;

/**
 * http echo server
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/18 23:25
 */
@Slf4j
public class NettyHttpEchoServer {
    public static void main(String[] args) {
        new NettyHttpEchoServer().runServer();
    }

    private void runServer() {
        // 启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 事件轮询组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 设置事件轮询组
        serverBootstrap.group(bossGroup, workerGroup);
        // 设置通道类型
        serverBootstrap.channel(NioServerSocketChannel.class);
        // 监听端口
        serverBootstrap.localAddress(NioConfig.SOCKET_SERVER_PORT);
        // 设置通道的参数
        serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        // 装配子通道流水线
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline()
                        .addLast(new HttpRequestDecoder())
                        .addLast(new HttpObjectAggregator(65535))
                        .addLast(new HttpResponseEncoder())
                        .addLast(new HttpEchoServerHandler());
            }
        });
        //
        try {
            ChannelFuture future = serverBootstrap.bind().sync();
            log.info("Server start success.");
            // 等待通道关闭的异步任务结束
            ChannelFuture closeFuture = future.channel().closeFuture();
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
