package moster.infras.core.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于事件顺序的执行器。
 *
 * @author zhangfei
 */
public class OrderedEventExecutor implements EventExecutor, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(OrderedEventExecutor.class);

    /**
     * 存放任务的队列容量，防止潜在的OOM。
     */
    private static final int QUEUE_CAPACITY = 10000;

    /**
     * 存放所有任务的队列。
     */
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    /**
     * 当前执行器所属的组。
     */
    private EventExecutorGroup<OrderedEventExecutor> group;

    /**
     * 实际上执行所有任务的线程。
     */
    private Thread thread;

    /**
     * 当前的负载。
     * 执行器会被分配给其它对象使用。
     * 每次分配给对象时，负载值加1；每次对象归还时，负载值减1。
     */
    private final AtomicInteger load = new AtomicInteger();

    /**
     * 执行器是否处于存活状态。
     */
    private volatile boolean alive;

    @Override
    public void execute(Runnable task) {
        try {
            this.queue.put(task);
        } catch (InterruptedException e) {
            logger.error("执行器[{}]在将任务入队列时被打断", this.thread.getName());
        }
    }

    @Override
    public void run() {
        for (; ; ) {
            if ((queue.size() == 0) && !this.alive) {
                return;
            }

            Runnable task = null;
            try {
                task = this.queue.take();
            } catch (InterruptedException e) {
                logger.error("执行器[{}]在取任务时收到打断信号", this.thread.getName());
                continue;
            }

            try {
                task.run();
            } catch (Throwable e) {
                logger.error("执行器[{}]运行任务[{}]时出现异常", this.thread.getName(), task.getClass().getName(), e);
            }
        }
    }

    @Override
    public int getLoad() {
        return this.load.get();
    }

    public void increaseLoad() {
        this.load.incrementAndGet();
    }

    public void decreaseLoad() {
        this.load.decrementAndGet();
    }

    @Override
    public EventExecutorGroup<OrderedEventExecutor> getGroup() {
        return this.group;
    }

    @Override
    public void release() {
        this.group.returnExecutor(this);
    }

    /**
     * 启动执行器。
     *
     * @param thread 执行器关联的线程
     */
    public void start(Thread thread) {
        this.thread = thread;
        this.alive = true;
        this.thread.start();
    }

    /**
     * 关闭执行器。
     */
    public void stop() {
        // 执行器的任务队列可能为空，而导致执行器的线程处于等待中。
        // 给与一个空任务，让执行器所在的线程被唤醒，从而继续在for循环中进行条件检查。
        this.alive = false;
        Runnable emptyTask = () -> {
            logger.info("执行器[{}]处理关闭时的空任务", this.thread.getName());
        };
        execute(emptyTask);

        try {
            this.thread.join();
        } catch (InterruptedException e) {
            logger.error("执行器[{}]在关闭时被打断", this.thread.getName());
        }
    }

    public OrderedEventExecutor() {
//        this.thread = thread;
    }

    public void setGroup(EventExecutorGroup<OrderedEventExecutor> group) {
        this.group = group;
    }

}
