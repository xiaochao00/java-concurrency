package org.ex.boys.concurrency.nio.ch5;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.ex.boys.concurrency.nio.NioConfig;
import org.ex.boys.concurrency.nio.common.util.DateUtil;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * netty echo client
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/14 23:34
 */
@Slf4j
public class NettyEchoClient {
    public static void main(String[] args) {
        new NettyEchoClient(NioConfig.SOCKET_SERVER_IP, NioConfig.SOCKET_SERVER_PORT).runClient();
    }

    private final int serverPort;
    private final String serverAddress;
    Bootstrap bootstrap = new Bootstrap();

    public NettyEchoClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void runClient() {
        // 创建反应器轮询组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            // 设置反引起轮询组
            bootstrap.group(eventExecutors);
            // 设置NIO类型的通道
            bootstrap.channel(NioSocketChannel.class);
            // 连接远程服务器
            bootstrap.remoteAddress(new InetSocketAddress(this.serverAddress, this.serverPort));
            // 设置通道的参数
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            // 装配子通道流水线
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(NettyEchoClientHandler.INSTANCES);
                }
            });
            //
            ChannelFuture f = bootstrap.connect();
            f.addListener((ChannelFuture futureListener) -> {
                if (futureListener.isSuccess()) {
                    log.info("EchoClient 连接成功.");
                } else {
                    log.info("EchoClient 客户端连接失败.");
                }
            });
            // 阻塞，直到连接成功
            f.sync();
            Channel channel = f.channel();

            Scanner scanner = new Scanner(System.in);
            log.info("请输入发送内容");
            while (scanner.hasNext()) {
                String content = scanner.next();
                byte[] bytes = (DateUtil.getNow() + ">>" + content).getBytes(StandardCharsets.UTF_8);
                // 发送 byteBuf
                ByteBuf buf = channel.alloc().buffer();
                buf.writeBytes(bytes);
                channel.writeAndFlush(buf);
                log.info("请输入发送内容");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

}
