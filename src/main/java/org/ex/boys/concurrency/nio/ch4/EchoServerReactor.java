package org.ex.boys.concurrency.nio.ch4;

import lombok.extern.slf4j.Slf4j;
import org.ex.boys.concurrency.nio.NioConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * echo server
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/11 10:47
 */
@Slf4j
public class EchoServerReactor implements Runnable {
    Selector selector;
    ServerSocketChannel serverSocketChannel;

    EchoServerReactor() throws IOException {
        //1.获取选择器
        selector = Selector.open();
        //2.获取通道
        serverSocketChannel = ServerSocketChannel.open();
        //3.设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //4.绑定连接
        serverSocketChannel.socket().bind(new InetSocketAddress(NioConfig.SOCKET_SERVER_IP,NioConfig.SOCKET_SERVER_PORT));
        log.info("Success start server");
        //5.注册通道类型，将接收新连接的事件注册到选择器上；
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6.注册绑定接收处理器，到selectKey
        selectionKey.attach(new AcceptHandler());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    //反应器负责转发收到的事件
                    dispatch(key);
                }
                keys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反应器的事件分发
     *
     * @param key 对应的IO事件
     */
    void dispatch(SelectionKey key) {
        Runnable handler = (Runnable) key.attachment();
        if (handler != null) {
            handler.run();
        }
    }

    /**
     * 处理连接事件的handler
     */
    class AcceptHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null) {
                    log.info("Success build a new connect with:{}.", channel.getRemoteAddress());
                    //创建一个处理echo的handler
                    new EchoHandler(selector, channel);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new EchoServerReactor()).start();
    }
}
