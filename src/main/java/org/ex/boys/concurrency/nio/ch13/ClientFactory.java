package org.ex.boys.concurrency.nio.ch13;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * curator client
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/24 18:20
 */
public class ClientFactory {
    public static CuratorFramework createSimple(String connectingString) {
        //重试测略
        ExponentialBackoffRetry retryPolice = new ExponentialBackoffRetry(1000, 3);
        //创建
        return CuratorFrameworkFactory.builder().connectString(connectingString).retryPolicy(retryPolice).build();
    }
}
