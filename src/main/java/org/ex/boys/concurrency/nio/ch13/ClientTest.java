package org.ex.boys.concurrency.nio.ch13;

import org.junit.Test;

/**
 * client test
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/24 19:25
 */
public class ClientTest {
    @Test
    public void testClient() {
        MyZkClient zkClient = MyZkClient.INSTANCE;
        String path1 = "/test/node";
        zkClient.isNodeExist(path1);
//        zkClient.createNode(path1, "test data");
        zkClient.readNode(path1);
        zkClient.destroy();
    }

}
