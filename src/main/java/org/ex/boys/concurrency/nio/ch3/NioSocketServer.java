package org.ex.boys.concurrency.nio.ch3;

import lombok.extern.slf4j.Slf4j;
import org.ex.boys.concurrency.common.IOUtil;
import org.ex.boys.concurrency.nio.NioConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * socket channel server
 * 服务端接收客户端发送的文件
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/1 0:13
 */
@Slf4j
public class NioSocketServer {
    public static void main(String[] args) throws IOException {
        NioSocketServer socketServer = new NioSocketServer();
        socketServer.startServer();
    }

    private static final Charset charset = StandardCharsets.UTF_8;
    private static final ByteBuffer byteBuffer = ByteBuffer.allocate(NioConfig.SERVER_BUFFER_SIZE);

    static class Client {
        // 文件名称
        String fileName;
        // 长度
        long fileLength;
        // 开始传输的时间
        long startTime;
        // 客户端的地址
        InetSocketAddress remoteAddress;
        // 输出的文件通道
        FileChannel fileChannel;
        // 接收的长度
        long receiveLength;

        public boolean isFinished() {
            return receiveLength >= fileLength;
        }
    }

    Map<SelectableChannel, Client> clientMap = new HashMap<>();

    public void startServer() throws IOException {
        // 1.创建获取选择器
        Selector selector = Selector.open();
        // 2.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 3.设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 4.绑定链接
        InetSocketAddress address = new InetSocketAddress(NioConfig.SOCKET_ADDRESS_PORT);
        serverSocket.bind(address);
        // 5.将通道绑定到选择器上，并注册IO事件为 接收新连接
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("serverChannel is listener...");
        // 6.轮询
        while (selector.select() > 0) {
            // 7.获取当前的事件集合
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                // 8.获取单个事件，并处理
                SelectionKey key = it.next();
                // 9.判断具体的事件
                if (key.isAcceptable()) {
                    // 10.当前是一个新的连接请求
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = server.accept();
                    if (clientChannel == null) {
                        continue;
                    }
                    // 11.新的客户端，切换为非阻塞模式
                    clientChannel.configureBlocking(false);
                    // 12.将当前的客户端注册到原则器上
                    SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                    // 13.业务处理
                    Client client = new Client();
                    client.remoteAddress = (InetSocketAddress) clientChannel.getRemoteAddress();
                    clientMap.put(clientChannel, client);
                    log.info("Success connect with client:{}.", clientChannel.getRemoteAddress());
                } else if (key.isReadable()) {
                    // 14.读取管道数据，写入文件
                    processData(key);
                }
                it.remove();
            }
        }
    }

    private void processData(SelectionKey key) {
        log.info("Begin process data...");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Client client = clientMap.get(socketChannel);
        int num;
        try {
            byteBuffer.clear();
            while ((num = socketChannel.read(byteBuffer)) > 0) {
                byteBuffer.flip();
                // 处理客户端发送的信息
                // 文件名的长度
                if (null == client.fileName) {
                    if (byteBuffer.capacity() < 4) {
                        continue;
                    }
                    int fileNameLength = byteBuffer.getInt();
                    byte[] fileNameBytes = new byte[fileNameLength];
                    byteBuffer.get(fileNameBytes);
                    //
                    String fileName = new String(fileNameBytes, charset);
                    File fileDir = new File(NioConfig.SERVER_SOCKET_PATH);
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    log.info("Sent file:{} to dir:{}.", fileName, fileDir.getAbsolutePath());

                    client.fileName = fileName;
                    String fullName = fileDir.getAbsolutePath() + File.separator + fileName;
                    log.info("Sent to file:{}.", fullName);

                    File newFile = new File(fileName);
                    if (!newFile.exists()) {
                        newFile.createNewFile();
                    }

                    client.fileChannel = new FileOutputStream(newFile).getChannel();

                    if (byteBuffer.capacity() < 8) {
                        continue;
                    }

                    // 文件长度
                    long contentLength = byteBuffer.getLong();
                    client.fileLength = contentLength;
                    log.info("Get the file length:{}.", contentLength);

                    client.startTime = System.currentTimeMillis();
                    log.info("Begin receive content...");

                    client.receiveLength += byteBuffer.capacity();
                    if (byteBuffer.capacity() > 0) {
                        client.fileChannel.write(byteBuffer);
                    }
                } else {
                    client.receiveLength += byteBuffer.capacity();
                    client.fileChannel.write(byteBuffer);
                }
                //s
                if (client.isFinished()) {
                    finished(key, client);
                }
                //
                byteBuffer.clear();
            }
            key.cancel();
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return;
        }
        //
        if (num == -1) {
            finished(key, client);
            byteBuffer.clear();
        }

    }

    private void finished(SelectionKey key, Client client) {
        IOUtil.closeQuietly(client.fileChannel);
        log.info("Done upload");
        key.cancel();
        log.info("Success receive file to:{}.", client.fileName);
        log.info("Size:{}.", client.fileLength);
        long endTime = System.currentTimeMillis();
        log.info("Total time cost:{} ms.", endTime - client.startTime);
    }
}
