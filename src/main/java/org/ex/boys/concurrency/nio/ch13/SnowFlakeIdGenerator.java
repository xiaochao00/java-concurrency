package org.ex.boys.concurrency.nio.ch13;

import lombok.extern.slf4j.Slf4j;

/**
 * snow flake id generator
 * <p>
 * 第一位+时间戳+机器码+序列号
 * 第一位 0 暂不用
 * 时间戳 41位
 * 机器码 10位
 * 序列号 12位，每毫秒同一节点从0开始往上加
 * <p>
 * 可微调上述位数
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/25 9:57
 */
@Slf4j
public class SnowFlakeIdGenerator {
    public static final SnowFlakeIdGenerator INSTANCE = new SnowFlakeIdGenerator();

    private SnowFlakeIdGenerator() {
        this.init();
    }

    private void init() {
        // 获取机器码，需要保证不能超过最大值
        this.workerId = ZkIdWorker.INSTANCE.getNodeId();
        log.info("Get workerId:{} for IDGenerator.", this.workerId);
        this.workerId = this.workerId << WORKER_ID_SHIFT;
        //
    }

    /**
     * 当前算法的开始时间 2021-07-25 11:06:46
     */
    private static final long START_TIMESTAMP = 1627182406362L;

    /**
     * 机器码，当前允许workerId最大值为 8192
     */
    private static final long WORKER_ID_BITS = 13;
    /**
     * 序列号，单节点最高支持每毫秒生成的ID数1024
     */
    private static final long SEQUENCE_ID_BITS = 10;
    /**
     * 最大的机器码，workId
     * -1的二进制码(补码)，右移13位补出来后13位全0，前面全1；然后取反，后13位全1
     */
    private static final long MAX_WORKER_ID = ~(-1 << WORKER_ID_BITS);
    /**
     * 最大的序列号
     */
    private static final long MAX_SEQUENCE_ID = ~(-1 << SEQUENCE_ID_BITS);
    /**
     * workerId的偏移位数
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_ID_BITS;
    /**
     * 时间戳的偏移位数
     */
    private static final long TIMESTAMP_ID_SHIFT = SEQUENCE_ID_BITS + WORKER_ID_BITS;
    /**
     * 机器码
     */
    private Long workerId;
    /**
     * 上一次的时间戳
     */
    private long preTimestamp = -1L;
    /**
     * 当前时间戳的序列号
     */
    private long curSequence = 0L;

    public Long nextId() {
        return generateId();
    }

    private synchronized long generateId() {
        long curTimestamp = System.currentTimeMillis();
        if (curTimestamp < preTimestamp) {
            log.error("Current timestamp:{} is less then preTimestamp:{}.", curTimestamp, preTimestamp);
            return -1L;
        }
        if (curTimestamp == preTimestamp) {
            if (curSequence < MAX_SEQUENCE_ID) {
                curSequence = curSequence + 1;
            } else {
                curTimestamp = this.nextTimestamp(curTimestamp);
                curSequence = 0;
            }
        } else {
            curSequence = 0;
        }
        this.preTimestamp = curTimestamp;
        //
        long time = (curTimestamp - START_TIMESTAMP) << TIMESTAMP_ID_SHIFT;
        return time | this.workerId | this.curSequence;
    }

    /**
     * 阻塞获取下一毫秒值
     *
     * @param timestamp 当前毫秒值
     * @return 下一毫秒值
     */
    private long nextTimestamp(long timestamp) {
        long curTimestamp = System.currentTimeMillis();
        while (curTimestamp == timestamp) {
            curTimestamp = System.currentTimeMillis();
        }
        return curTimestamp;
    }


}
