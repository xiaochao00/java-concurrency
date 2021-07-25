package org.ex.boys.concurrency.nio.ch13;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * id worker
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/25 9:36
 */
@Slf4j
public class ZkIdWorker {
    transient MyZkClient zkClient;
    //
    private static String pathPrefix = "/test/IDMaker/worker-";
    /**
     * 当前注册的路径
     */
    private String registeredPath = null;
    /**
     * 保存当前节点的ID,不用每次重复计算
     */
    private Long nodeId;

    public static final ZkIdWorker INSTANCE = new ZkIdWorker();

    private ZkIdWorker() {
        this.zkClient = MyZkClient.INSTANCE;
        this.init();
    }

    private void init() {
        this.registeredPath = this.zkClient.createEphemeralSeqNode(pathPrefix, pathPrefix);
        if (registeredPath == null) {
            throw new RuntimeException("New node register fail:");
        }
        log.info("Success ZkIdWorker init");
    }

    public Long getNodeId() {
        if (Objects.nonNull(this.nodeId)) {
            return this.nodeId;
        }
        String idString = null;
        int index = this.registeredPath.lastIndexOf(pathPrefix);
        if (index >= 0) {
            index += pathPrefix.length();
            idString = index <= registeredPath.length() ? registeredPath.substring(index) : null;
        }
        //
        if (idString == null) {
            throw new RuntimeException("Fail when generate the node id.");
        }
        //
        this.nodeId = Long.parseLong(idString);
        log.info("Success generate nodeId:{}.", this.nodeId);
        return this.nodeId;
    }
}
