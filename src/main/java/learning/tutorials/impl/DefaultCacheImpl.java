package learning.tutorials.impl;

import learning.tutorials.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;

public class DefaultCacheImpl<K, V> implements Cache<K, V> {

    private final Logger logger = Logger.getLogger(DefaultCacheImpl.class.getName());

    private static final Object OBJ_LOCK = new Object();

    private AtomicInteger callCount = new AtomicInteger(0);

    private final Function<K, V> kvFunction;

    private final Map<K, V> localCache;

    public DefaultCacheImpl(Function<K, V> kvFunction) {
        if (kvFunction == null) {
            throw new IllegalArgumentException("Function to provide value cannot be null");
        }
        this.kvFunction = kvFunction;

        // Not using concurrent hashmap as advised in the problem description
        this.localCache = new HashMap<>();
    }

    @Override
    public V get(K key) {
        return getWithSynchronizedBlock(key);
    }

    /**
     * @param key
     * @return
     */
    private V getWithSynchronizedBlock(K key) {

        if (key == null)
        {
            return null;
        }

        synchronized (OBJ_LOCK) {
            if (!localCache.containsKey(key)) {
                var value = kvFunction.apply(key);
                localCache.put(key, value);
                logger.info(String.format("Method with sunchronized block so far called: %d times. Key added is %s", callCount.incrementAndGet(), key));
            }
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


