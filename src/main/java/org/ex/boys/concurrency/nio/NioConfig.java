package org.ex.boys.concurrency.nio;

import org.ex.boys.concurrency.nio.common.ConfigProperties;

/**
 * https://gitee.com/crazymaker/netty_redis_zookeeper_source_code
 * nio config
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/1 0:21
 */
public class NioConfig extends ConfigProperties {
    static ConfigProperties singleton = new NioConfig("/system.properties");
    public static final String SOCKET_SEND_FILE = singleton.getValue("socket.send.file");
    public static final String SOCKET_RECEIVE_FILE = singleton.getValue("socket.receive.file");
    public static final String SOCKET_SERVER_IP = singleton.getValue("socket.server.ip");
    public static final int SOCKET_SERVER_PORT = Integer.parseInt(singleton.getValue("socket.server.port"));
    public static final int SEND_BUFFER_SIZE = Integer.parseInt(singleton.getValue("send.buffer.size"));
    public static final int SERVER_BUFFER_SIZE = Integer.parseInt(singleton.getValue("server.buffer.size"));
    public static final String SERVER_SOCKET_PATH = singleton.getValue("socket.receive.path");

    private NioConfig(String s) {
        super(s);
        super.loadFromFile();
    }
}
