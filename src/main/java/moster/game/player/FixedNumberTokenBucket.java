package moster.game.player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 固定数量的令牌桶。
 *
 * 桶在初始化时，会放入固定数量的令牌。
 * 在使用时从桶中取走令牌，不用时需要将令牌归还到桶中。
 *
 * 注意：令牌的获取、归还逻辑，使用的是类似自旋锁的方式来实现的。
 * 在使用这个类时，需要自行根据并发情况来判断是否合适。
 *
 * @author zhangfei
 */
public class FixedNumberTokenBucket {

    /**
     * 将值设置为当前线程，表示获取了锁。
     */
    private final AtomicReference<Thread> reference = new AtomicReference<>();

    /**
     * 桶的容量，也就是持有大令牌的最大数量。
     */
    private final int capacity;

    /**
     * 当前剩余的令牌数量。
     */
    private final AtomicInteger remain;

    /**
     * 尝试获取一个令牌。
     *
     * @return 如果获取成功就返回true，否则返回false
     */
    public boolean tryAcquire() {
        Thread thread = Thread.currentThread();
        boolean success = true;

        for (; ; ) {
            boolean getLock = this.reference.compareAndSet(null, thread);
            if (getLock) {
                if (this.remain.get() <= 0) {
                    success = false;
                } else {
                    this.remain.decrementAndGet();
                }

                this.reference.set(null);
                break;
            }
        }

        return success;
    }

    /**
     * 获取一个令牌。
     */
    public void acquire() {
        for (; ; ) {
            boolean success = tryAcquire();
            if (success) {
                break;
            }
        }
    }

    /**
     * 归还一个令牌。
     * 注意：必须要保证在拥有令牌的情况下才能归还令牌。
     */
    public void release() {
        Thread thread = Thread.currentThread();

        for (; ; ) {
            boolean getLock = this.reference.compareAndSet(null, thread);
            if (getLock) {
                int num = this.remain.get();
                if (num < this.capacity) {
                    this.remain.incrementAndGet();
                }

                this.reference.set(null);
                break;
            }
        }
    }

    /**
     * @param capacity 桶的容量
     */
    public FixedNumberTokenBucket(int capacity) {
        this.capacity = capacity;
        this.remain = new AtomicInteger(capacity);
    }

}
