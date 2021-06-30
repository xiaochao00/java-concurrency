package org.ex.boys.concurrency.nio;

import org.ex.boys.concurrency.common.ConfigProperties;

/**
 * nio config
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/1 0:21
 */
public class NioConfig extends ConfigProperties {
    static ConfigProperties singleton = new NioConfig("/system.properties");
    public static final String SOCKET_SEND_FILE = singleton.getValue("socket.send.file");

    private NioConfig(String s) {
        super(s);
        super.loadFromFile();
    }
}
