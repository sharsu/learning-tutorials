package learning.tutorials.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class DefaultCacheImplTest {

    private Random random = new Random();

    private final Logger logger = Logger.getLogger(DefaultCacheImplTest.class.getName());

    @Test
    public void testConcurrentHashMapTimes() throws InterruptedException
    {
        var threadCount = 10;
        var callPerThread = 500;
        ConcurrentHashMap mapForTest = new ConcurrentHashMap();

        long timeMillis = System.currentTimeMillis();
        logger.info("Start time: [" + timeMillis + "]");
        var cache = new ConcurrentHashMap<Integer, Integer>();
        var latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int l = 0; l < callPerThread; l++) {
                    Integer key = getRandomNumber(10, 1000);
                    // Cache call
                    Integer value = cache.computeIfAbsent(key, o -> o * 10);
                    assertEquals(10 * key.intValue(), value.intValue());
                    mapForTest.putIfAbsent(key, new Object());
                }
                latch.countDown();
            }).start();
        }
        latch.await();

        List<Integer> expectedKeys = new ArrayList<>(mapForTest.keySet());
        Collections.sort(expectedKeys);

        List<Integer> actualKeys = new ArrayList<>(cache.keySet());
        Collections.sort(actualKeys);

        logger.info("Total time taken: [" + (System.currentTimeMillis() - timeMillis) + "]");

        logger.info("Cache size: " + actualKeys.size());

        assertEquals(expectedKeys.size(), actualKeys.size());

        assertEquals(expectedKeys, actualKeys);

    }


    @Test
    public void testDefaultImpl() throws InterruptedException {
        var threadCount = 10;
        var callPerThread = 500;
        ConcurrentHashMap mapForTest = new ConcurrentHashMap();

        long timeMillis = System.currentTimeMillis();
        logger.info("Start time: [" + timeMillis + "]");
        var cache = new DefaultCacheImpl<Integer, Integer>(i -> 10 * i);
        var latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int l = 0; l < callPerThread; l++) {
                    Integer key = getRandomNumber(10, 1000);
                    // Cache call
                    Integer value = cache.get(key);
                    assertEquals(10 * key.intValue(), value.intValue());
                    mapForTest.putIfAbsent(key, new Object());
                }
                latch.countDown();
            }).start();
        }
        latch.await();

        List<Integer> expectedKeys = new ArrayList<>(mapForTest.keySet());
        Collections.sort(expectedKeys);

        List<Integer> actualKeys = cache.getKeysAsList();
        Collections.sort(actualKeys);

        logger.info("Total time taken: [" + (System.currentTimeMillis() - timeMillis) + "]");

        logger.info("Cache size: " + cache.getSize());

        assertEquals(cache.getSize(), cache.numberOfMethodCalls());

        assertEquals(expectedKeys.size(), actualKeys.size());

        assertEquals(expectedKeys, actualKeys);
    }

    @Test
    public void testReentrantLockCacheImpl() throws InterruptedException {
        var threadCount = 10;
        var callPerThread = 500;
        ConcurrentHashMap mapForTest = new ConcurrentHashMap();

        long timeMillis = System.currentTimeMillis();
        logger.info("Start time: [" + timeMillis + "]");
        var cache = new ReentrantLockCacheImpl<Integer, Integer>(i -> 10 * i);
        var latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int l = 0; l < callPerThread; l++) {
                    Integer key = getRandomNumber(10, 1000);
                    // Cache call
                    Integer value = cache.get(key);

                    assertEquals(10 * key.intValue(), value.intValue());
                    mapForTest.putIfAbsent(key, new Object());
                }
                latch.countDown();
            }).start();
        }
        latch.await();

        List<Integer> expectedKeys = new ArrayList<>(mapForTest.keySet());
        Collections.sort(expectedKeys);

        List<Integer> actualKeys = cache.getKeysAsList();
        Collections.sort(actualKeys);

        logger.info("Total time taken: [" + (System.currentTimeMillis() - timeMillis) + "]");

        logger.info("Cache size: " + cache.getSize());

        assertEquals(cache.getSize(), cache.numberOfMethodCalls());

        assertEquals(expectedKeys.size(), actualKeys.size());

        assertEquals(expectedKeys, actualKeys);
    }


    @Test
    public void testVarHandleCacheImpl() throws InterruptedException {
        var threadCount = 10;
        var callPerThread = 500;
        ConcurrentHashMap mapForTest = new ConcurrentHashMap();

        long timeMillis = System.currentTimeMillis();
        logger.info("Start time: [" + timeMillis + "]");
        var cache = new VarHandleCacheImpl<Integer, Integer>(i -> 10 * i);
        var latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int l = 0; l < callPerThread; l++) {
                    // Cache call
                    Integer key = getRandomNumber(10, 1000);
                    Integer value = cache.get(key);

                    assertEquals(10 * key.intValue(), value.intValue());
                    // assertEquals(10 * key, value);
                    mapForTest.putIfAbsent(key, new Object());
                }
                latch.countDown();
            }).start();
        }
        latch.await();

        List<Integer> expectedKeys = new ArrayList<>(mapForTest.keySet());
        Collections.sort(expectedKeys);

        List<Integer> actualKeys = cache.getKeysAsList();
        Collections.sort(actualKeys);

        logger.info("Total time taken: [" + (System.currentTimeMillis() - timeMillis) + "]");

        logger.info("Cache size: " + cache.getSize());

        assertEquals(cache.getSize(), cache.numberOfMethodCalls());

        assertEquals(expectedKeys.size(), actualKeys.size());

        assertEquals(expectedKeys, actualKeys);
    }

    private void sleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
