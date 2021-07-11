package org.ex.boys.concurrency.nio.ch4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * echo handler
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/11 11:37
 */
@Slf4j
public class EchoHandler implements Runnable {
    final SelectionKey selectionKey;
    final SocketChannel channel;
    final ByteBuffer byteBuffer = ByteBuffer.allocate(2014);
    //处理器实例额状态，一个连接一个实例
    static final int RECEIVING = 0, SENDING = 1;
    int state = RECEIVING;

    EchoHandler(Selector selector, SocketChannel channel) throws IOException {
        this.channel = channel;
        channel.configureBlocking(false);
        this.selectionKey = channel.register(selector, 0);
        this.selectionKey.attach(this);
        // 注册读就绪事件
        this.selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            if (state == SENDING) {
                //从buffer中，写入数据到通道
                channel.write(byteBuffer);
                byteBuffer.clear();
                selectionKey.interestOps(SelectionKey.OP_READ);
                state = RECEIVING;
            } else if (state == RECEIVING) {
                int length = 0;
                //从通道中读取数据，到buffer
                while ((length = channel.read(byteBuffer)) > 0) {
                    log.info(new String(byteBuffer.array(), 0, length));
                }
                // 读完后，翻转byte buffer的模式
                byteBuffer.flip();
                //注册写就绪事件
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                //
                state = SENDING;
            }
            //
//            selectionKey.cancel();
        } catch (IOException e) {
            e.printStackTrace();
            selectionKey.cancel();
            try {
                channel.finishConnect();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
