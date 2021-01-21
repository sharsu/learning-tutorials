# learning-tutorials

## Pre-requisite
- Java 11
- Gradle

## Cache Implementation
There are 3 different approach to the cache implementation added in this repository.

### Using Synchronize Block

```java
        synchronized (OBJ_LOCK) {
            if (!localCache.containsKey(key)) {
                var value = kvFunction.apply(key);
                localCache.put(key, value);
                logger.info(String.format("Method with sunchronized block so far called: %d times. Key added is %s", callCount.incrementAndGet(), key));
            }
        }
```

### Using ReentrantLock
```java
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
``` 

### Using VarHandle
Cache using VarHandle is work in progress and not yet tested fully. 

## Deadline engine
Current implementation of the `DeadlineEngine` interface is a basic `PriorityBlockingQueue` backed deadline engine. 