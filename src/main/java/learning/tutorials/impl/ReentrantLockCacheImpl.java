package learning.tutorials.impl;

import learning.tutorials.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Logger;

public class ReentrantLockCacheImpl<K, V> implements Cache<K, V> {

    private final Logger logger = Logger.getLogger(ReentrantLockCacheImpl.class.getName());

    private ReentrantLock sharedLock = new ReentrantLock();

    private AtomicInteger callCount = new AtomicInteger(0);

    private final Function<K, V> kvFunction;

    private final Map<K, V> localCache;

    public ReentrantLockCacheImpl(Function<K, V> kvFunction) {
        if (kvFunction == null) {
            throw new IllegalArgumentException("Function to provide value cannot be null");
        }
        this.kvFunction = kvFunction;

        // Not using concurrent hashmap as advised in the problem description
        this.localCache = new HashMap<>();
    }

    @Override
    public V get(K key) {
        return getWithReentrantLock(key);
    }

    /**
     * @param key
     * @return
     */
    private V getWithReentrantLock(K key) {
        if (key == null)
        {
            return null;
        }

        try {
            sharedLock.lock();
            boolean inLocalCache = localCache.containsKey(key);
            if (!inLocalCache) {
                var value = kvFunction.apply(key);
                localCache.put(key, value);
                logger.info(String.format("Method with reentrant lock so far called: %d times. Key added is %s", callCount.incrementAndGet(), key));
            }
        } finally {
            sharedLock.unlock();
        }

        return localCache.get(key);
    }

    protected int getSize()
    {
        return localCache.size();
    }

    protected List<K> getKeysAsList()
    {
        return new ArrayList<>(localCache.keySet());
    }

    protected int numberOfMethodCalls()
    {
        return callCount.get();
    }
}


