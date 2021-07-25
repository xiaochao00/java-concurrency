package org.ex.boys.concurrency.nio.ch13;

import ch.qos.logback.core.util.CloseUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.ex.boys.concurrency.nio.NioConfig;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * my zk client
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/24 18:39
 */
@Slf4j
public class MyZkClient {
    private CuratorFramework client;
    public static final MyZkClient INSTANCE = new MyZkClient(NioConfig.ZK_SERVER_PATH);

    private MyZkClient(String connectingString) {
        log.info("Begin connect ZK:{}.", connectingString);
        this.client = ClientFactory.createSimple(connectingString);
        this.client.start();
        log.info("Success connect to zk.");
    }

    public void destroy() {
        CloseUtil.closeQuietly(this.client);
    }

    @SneakyThrows
    public void createNode(String path, String data) {
        byte[] replay = null;
        if (data != null) {
            replay = data.getBytes(StandardCharsets.UTF_8);
        }
        this.client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path, replay);
        log.info("Success create node:{} with data:{}.", path, data);
    }

    @SneakyThrows
    public String readNode(String path) {
        Stat stat = this.client.checkExists().forPath(path);
        if (stat != null) {
            byte[] bytes = this.client.getData().forPath(path);
            String data = new String(bytes, StandardCharsets.UTF_8);
            log.info("Get data:{} for path:{}.", data, path);
            return data;
        }
        return null;
    }

    @SneakyThrows
    public void updateNode(String path, String newData) {
        byte[] bytes = null;
        if (Objects.nonNull(newData)) {
            bytes = newData.getBytes(StandardCharsets.UTF_8);
        }
        this.client.setData().forPath(path, bytes);
        log.info("Success update path:{} to {}.", path, newData);
    }

    @SneakyThrows
    public void deleteNode(String path) {
        this.client.delete().forPath(path);
        log.info("Success delete path:{}.", path);
    }

    @SneakyThrows
    public String createEphemeralSeqNode(String path, String data) {
        byte[] bytes = null;
        if (Objects.nonNull(data)) {
            bytes = data.getBytes(StandardCharsets.UTF_8);
        }
        String registeredPath = this.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, bytes);
        log.info("Success create ephemeral sequential node:{} with data:{}.", path, data);
        return registeredPath;
    }

    @SneakyThrows
    public boolean isNodeExist(String path) {
        Stat stat = this.client.checkExists().forPath(path);
        log.info("The stat:{} for path:{}.", stat, path);
        return stat == null;
    }

}
