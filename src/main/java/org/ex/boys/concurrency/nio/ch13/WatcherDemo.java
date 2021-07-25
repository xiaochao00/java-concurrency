package org.ex.boys.concurrency.nio.ch13;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.Watcher;
import org.junit.AfterClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * watcher demo
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/25 12:08
 */
@Slf4j
public class WatcherDemo {

    private static final CuratorFramework zkClient = MyZkClient.INSTANCE.getClient();

    @AfterClass
    public static void close() {
        zkClient.close();
    }

    @SneakyThrows
    @Test
    public void watcherNode() {
        // 第一种模式 watcher模式:只会生效触发1次；除非生效后，在重新用usingWatch绑定
        Watcher w = watchedEvent -> log.info("Get the event:{}.", watchedEvent);
        String path = "/test/watcher/demo1";
        if (zkClient.checkExists().forPath(path) == null) {
            zkClient.create().creatingParentsIfNeeded().forPath(path, "DEFAULT".getBytes(StandardCharsets.UTF_8));
        }
        // GetDataBuilder,ExistDataBuilder,GetChildrenBuilder等实现了Watcher<T>接口的构造者实例，可以采用usingWatcher方法
        byte[] bytes = zkClient.getData().usingWatcher(w).forPath(path);
        String data = new String(bytes);
        log.info("Get path:{} data:{}.", path, data);
        // 第一次改变值
        zkClient.setData().forPath(path, "第一次改变".getBytes(StandardCharsets.UTF_8));
        // 第二次改变值
        zkClient.setData().forPath(path, "第二次改变".getBytes(StandardCharsets.UTF_8));
        //
        Thread.sleep(10000);
    }

    @SneakyThrows
    @Test
    public void nodeCache() {
        // 第二种模式，nodeCache模式，会触发多次
        String path = "/test/nodeCache/demo1";
        if (zkClient.checkExists().forPath(path) == null) {
            zkClient.create().creatingParentsIfNeeded().forPath(path, null);
        }
        // 第一步：创建NodeCache对象
        NodeCache nodeCache = new NodeCache(zkClient, path, false);
        // 第二步：创建listener对象
        NodeCacheListener listener = () -> {
            ChildData childData = nodeCache.getCurrentData();
            log.info("Node:{} data have changed.", childData.getPath());
            log.info("Data change to {}.", new String(childData.getData(), StandardCharsets.UTF_8));
            log.info("Node stat:{}.", childData.getStat());
        };
        // 第三步：添加listener到NodeCache对象
        nodeCache.getListenable().addListener(listener);
        // 第四步：启动事件监听
        nodeCache.start();
        log.info("Begin node cache listener.");
        // 第一次节点变更
        zkClient.setData().forPath(path, "第一次改变".getBytes(StandardCharsets.UTF_8));
        // 第二次节点变更
        zkClient.setData().forPath(path, "第二次改变".getBytes(StandardCharsets.UTF_8));
        //
        Thread.sleep(10000);
    }

    @SneakyThrows
    @Test
    public void pathCache() {
        // 第二种模式，nodeCache模式的第二种，会触发多次
        String path = "/test/nodeCache/demo2";
        String childPath = "/test/nodeCache/demo2/demo#";
        if (zkClient.checkExists().forPath(path) == null) {
            zkClient.create().creatingParentsIfNeeded().forPath(path, null);
        }
        // 创建 PathNodeCache对象
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, false);
        //
        PathChildrenCacheListener listener = (curatorFramework, event) -> {
            log.info("Happened event type:{}", event.getType());
            ChildData data = event.getData();
            byte[] bytes = data.getData();
            if (bytes != null) {
                log.info("Path data:{}.", new String(bytes, StandardCharsets.UTF_8));
            } else {
                log.info("Path data is null.");
            }
        };
        //
        pathChildrenCache.getListenable().addListener(listener);
        //
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        Thread.sleep(10000);
        // 创建3个节点
        for (int i = 0; i < 3; i++) {
            zkClient.create().forPath(childPath + i, null);
        }
        Thread.sleep(10000);
        // 更新
        for (int i = 0; i < 3; i++) {
            zkClient.setData().forPath(childPath + i, Integer.toString(i).getBytes(StandardCharsets.UTF_8));
        }
        Thread.sleep(10000);
        // 删除
        for (int i = 0; i < 3; i++) {
            zkClient.delete().forPath(childPath + i);
        }
        Thread.sleep(10000);
    }

    // 省略 TreeCache =(NodeCache+PathCache)
    // NodeCache 仅监听当前节点
    // PathCache 仅监听当前节点的直接子节点
    // TreeCache 可以控制监听的深度
}
