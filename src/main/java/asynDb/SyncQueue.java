package asynDb;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SyncQueue implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(SyncQueue.class);
    private static AsynDBEntity SHUTDOWN_ENTITY = new AsynDBEntity();
    private final int queueId;
    private volatile boolean stop = false;
    private volatile long sysCount = 0;
    private final BlockingQueue<AsynDBEntity> asynQueue = new LinkedBlockingQueue<>();
    private ISyncStrategy iSyncStrategy;
    private volatile long preNum;

    public SyncQueue(int queueId, ISyncStrategy iSyncStrategy) {
        this.queueId = queueId;
        this.iSyncStrategy = iSyncStrategy;
    }

    public boolean submit(AsynDBEntity asynDBEntity) {
        if (stop) {
            return false;
        }
        return asynQueue.add(asynDBEntity);
    }


    public boolean shutDown(long mills) {
        if (stop) {
            return false;
        }
        stop = true;
        asynQueue.add(SHUTDOWN_ENTITY);
        return true;
    }

    public int getWaitSize() {
        return asynQueue.size();
    }

    public int getQueueId() {
        return queueId;
    }

    public long getSysCount() {
        return sysCount;
    }

    public SyncStats stats() {
        long total = this.getSysCount();
        long perioNum = total - preNum;
        preNum = total;
        int waitinng = getWaitSize();
        return new SyncStats(waitinng, total, perioNum);
    }

    @Override
    public void run() {
        while (true) {
            final int numLoops = iSyncStrategy.getNumEachLoop();
            final int tryTimes = iSyncStrategy.tryTimes();
            for (int i = 0; i < numLoops; i++) {
                AsynDBEntity entity = null;
                try {
                    entity = asynQueue.take();
                } catch (InterruptedException e) {
                    logger.error("SyncDbInterruptedException {}", queueId, e);
                }
                if (entity == null || entity == SHUTDOWN_ENTITY) {
                    break;
                }
                try {
                    entity.trySync(tryTimes);
                    sysCount++;
                } catch (Exception e) {
                    throw new SyncException("ss", e);
                }
            }
            if (stop) {
                if (!asynQueue.isEmpty()) {
                    continue;
                }
                break;
            } else {
                try {
                    int waitSize = asynQueue.size();
                    long sleepTime = iSyncStrategy.getSleepTime(waitSize);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
