package org.ex.boys.concurrency.nio.ch3;

import lombok.extern.slf4j.Slf4j;
import org.ex.boys.concurrency.common.IOUtil;
import org.ex.boys.concurrency.nio.NioConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * socket channel client
 * 上传文件到服务端
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/1 0:10
 */
@Slf4j
public class NioSocketClient {
    public static void main(String[] args) {
        NioSocketClient client = new NioSocketClient();
        client.sendFile();
    }

    private static final Charset charset = StandardCharsets.UTF_8;

    /**
     * 发送文件到远端
     * 1.发送目的文件的名称的长度；
     * 2.发送目的文件的字符串值；
     * 3.发送文件的大小长度；
     * 4.发送文件的内容；
     * 5.关闭通道；
     */
    public void sendFile() {
        log.info("================开始发送================");
        String sourcePath = NioConfig.SOCKET_SEND_FILE;
        String srcPath = IOUtil.getResourcePath(sourcePath);
        log.info("srcPath:{}.", srcPath);

        String destName = NioConfig.SOCKET_RECEIVE_FILE;
        log.info("destName:{}.", destName);

        File file = new File(srcPath);
        if (!file.exists()) {
            log.error("Src path:{} don`t exist.", srcPath);
            return;
        }

        String socketAddress = NioConfig.SOCKET_ADDRESS;
        int socketAddressPort = NioConfig.SOCKET_ADDRESS_PORT;
        long fileSize = file.length();

        try {
            FileChannel fileChannel = new FileInputStream(file).getChannel();

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.socket().connect(new InetSocketAddress(socketAddress, socketAddressPort));
            socketChannel.configureBlocking(false);
            log.info("Client connect to server:{}:{} success.", socketAddress, socketAddressPort);

            while (!socketChannel.finishConnect()) {
                // 不断自旋，等待，直到连接上
            }

            // 发送文件名称和长度
            ByteBuffer buffer = sendNameAndLength(destName, fileSize, socketChannel);
            // 发送内容
            int contentLength = sendContent(fileSize, fileChannel, socketChannel, buffer);
            if (contentLength == -1) {
                IOUtil.closeQuietly(fileChannel);
                socketChannel.shutdownOutput();
                IOUtil.closeQuietly(socketChannel);
            }

            log.info("===================Success send file:{}.============================", srcPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int sendContent(long fileSize, FileChannel fileChannel, SocketChannel socketChannel, ByteBuffer byteBuffer) throws IOException {
        // 发送文件内容
        log.info("Begin send content...");
        int length;
        long progress = 0;
        while ((length = fileChannel.read(byteBuffer)) > 0) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            progress += length;
            log.info("| Current progress:{}% |.", 100 * progress / fileSize);
        }
        log.info("Total send file size:{}.", length);
        return length;
    }

    /**
     * 发送文件名的长度，文件名字符串，文件的大小 字段
     *
     * @param destName      输出目的的文件名
     * @param fileSize      文件的大小
     * @param socketChannel socket channel 对象
     * @return buffer
     * @throws IOException 文件异常
     */
    private ByteBuffer sendNameAndLength(String destName, long fileSize, SocketChannel socketChannel) throws IOException {
        log.info("Begin send name:{} and fileSize:{}.", destName, fileSize);
        ByteBuffer fileNameByteBuffer = charset.encode(destName);

        ByteBuffer buffer = ByteBuffer.allocate(NioConfig.SEND_BUFFER_SIZE);
        // 发送文件名称的长度
        buffer.putInt(fileNameByteBuffer.capacity());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
        log.info("Done send length of fileName:{}.", destName);
        // 发送文件名称
        socketChannel.write(fileNameByteBuffer);
        log.info("Done send fileName:{}.", destName);
        // 发送文件长度
        buffer.putLong(fileSize);
        buffer.flip();
        socketChannel.write(buffer);
        log.info("Done send fileLength:{}.", fileSize);
        //
        buffer.clear();
        log.info("Finish send destName and fileSize.");
        return buffer;
    }
}
