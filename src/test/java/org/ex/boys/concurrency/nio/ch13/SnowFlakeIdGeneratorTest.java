package org.ex.boys.concurrency.nio.ch13;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SnowFlakeIdGeneratorTest {
    @Test
    public void testSnowFlakeId() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final HashSet<Long> idSet = new HashSet<>();
        final Collection<Long> syncIdSet = Collections.synchronizedCollection(idSet);
        long start = System.currentTimeMillis();
        log.info("Begin generate id");
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 1000; j++) {
                    Long id = SnowFlakeIdGenerator.INSTANCE.nextId();
                    syncIdSet.add(id);
                }
            });
        }
        //
        executorService.shutdown();
        try {
            boolean res = executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        long end = System.currentTimeMillis();
        log.info("Generate id count:{}.", syncIdSet.size());
        log.info("Time cost:{} ms.", (end - start));
        log.info("Some id:{}.", syncIdSet.toString());
    }

}