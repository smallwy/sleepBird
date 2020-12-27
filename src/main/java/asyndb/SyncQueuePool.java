package asyndb;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.concurrent.*;

public class SyncQueuePool {
    private final static Logger logger = LoggerFactory.getLogger(SyncQueuePool.class);

    private SyncQueue[] pool;
    private int poolSize;
    private ScheduledExecutorService monitor;
    private final ExecutorService workExecutors;
    private volatile boolean stop;

    public SyncQueuePool(ThreadFactory threadFactory, int poolSize, ISyncStrategy syncStrategy) {
        super();
        workExecutors = Executors.newFixedThreadPool(poolSize, threadFactory);
        pool = new SyncQueue[poolSize];
        this.poolSize = poolSize;
        for (int i = 0; i < poolSize; i++) {
            pool[i] = new SyncQueue(i, syncStrategy);
            workExecutors.execute(pool[i]);
        }

        monitor = Executors.newScheduledThreadPool(1, new SimpleThreadFactory());
        monitor.scheduleWithFixedDelay(() -> {
            int totalWait = 0;
            for (SyncQueue syncQueue : pool) {
                SyncStats syncStats = syncQueue.stats();
                logger.debug(syncStats.toString());
            }
        }, 0, 0, TimeUnit.SECONDS);
    }


    public boolean submit(AsynDBEntity asynDBEntity) {
        if (stop) {
            return false;
        }

        SyncQueue syncQueue = asynDBEntity.getSyncQueue();
        if (syncQueue == null) {
            int hash = asynDBEntity.getHash() % poolSize;
            hash = Math.abs(hash);
            syncQueue = pool[hash];
            asynDBEntity.setSyncQueue(syncQueue);
        }
        return syncQueue.submit(asynDBEntity);
    }

    public boolean shutDown(long millis) throws InterruptedException {
        stop = true;
        for (int i = 0; i < poolSize; i++) {
            pool[i].shutDown(millis);
        }
        workExecutors.shutdown();
        workExecutors.awaitTermination(millis, TimeUnit.MILLISECONDS);
        monitor.shutdown();
        return true;
    }
}
