package asynDb;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private ThreadGroup threadGroup;
    private String groupName;

    public SimpleThreadFactory() {
        init();
        groupName = threadGroup.getName();

    }


    public  SimpleThreadFactory(String groupName) {
        init();
        groupName = groupName;
    }

    public void init() {
        Executors.defaultThreadFactory();
        SecurityManager securityManager = System.getSecurityManager();
        threadGroup = securityManager == null ? Thread.currentThread().getThreadGroup() : securityManager.getThreadGroup();
    }


    @Override
    public Thread newThread(Runnable r) {
        String threadName = groupName + "_thread" + threadNumber.getAndIncrement();
        Thread t = new Thread(threadGroup, r, threadName);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
