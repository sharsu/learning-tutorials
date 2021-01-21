package learning.tutorials.impl;

import learning.tutorials.Cache;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;

public class VarHandleCacheImpl<K, V> implements Cache<K, V> {

    private final Logger logger = Logger.getLogger(VarHandleCacheImpl.class.getName());

    private AtomicInteger callCount = new AtomicInteger(0);

    private final Function<K, V> kvFunction;

    private volatile K currentKey;

    private Map<K, V> localCache;

    private static final VarHandle CURRENT_KEY;

    public VarHandleCacheImpl(Function<K, V> kvFunction) {
        if (kvFunction == null) {
            throw new IllegalArgumentException("Function to provide value cannot be null");
        }
        this.kvFunction = kvFunction;

        this.localCache = new HashMap<>();
        this.currentKey = null;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }

        if (localCache.containsKey(key)) {
            return localCache.get(key);
        }

        casCurrentKey(key);
        if (!localCache.containsKey(key)) {
            localCache.put(key, kvFunction.apply(key));
            logger.info(String.format("Method with varHandle so far called: %d times. Key added is %s", callCount.incrementAndGet(), key));
        }

        return localCache.get(key);
    }

    protected int getSize() {
        return localCache.size();
    }

    protected List<K> getKeysAsList() {
        return new ArrayList<>(localCache.keySet());
    }

    protected int numberOfMethodCalls() {
        return callCount.get();
    }

    private boolean casCurrentKey(K key) {
        return CURRENT_KEY.compareAndSet(this, currentKey, key);
    }

    static {
        try {
            Lookup l = MethodHandles.lookup();

            CURRENT_KEY = l.findVarHandle(VarHandleCacheImpl.class, "currentKey", Object.class);

        } catch (ReflectiveOperationException var1) {
            throw new ExceptionInInitializerError(var1);
        }
    }

    public void close() {
        CURRENT_KEY.weakCompareAndSetRelease();
    }

}
