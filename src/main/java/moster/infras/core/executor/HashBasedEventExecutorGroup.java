package moster.infras.core.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;

/**
 * 基于hash的执行器组。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class HashBasedEventExecutorGroup implements EventExecutorGroup<OrderedEventExecutor> {

    private static final Logger logger = LoggerFactory.getLogger(HashBasedEventExecutorGroup.class);

    /**
     * 持有的所有执行器。
     */
    private OrderedEventExecutor[] pool;

    /**
     * 线程前缀名称。
     */
    private final String prefix;

    /**
     * 执行器数量。
     */
    private final int count;

    @Override
    public OrderedEventExecutor borrowExecutor() {
        return borrowExecutor(0);
    }

    @Override
    public OrderedEventExecutor borrowExecutor(int arg) {
        int index = hash(arg);
        OrderedEventExecutor executor = this.pool[index];
        executor.increaseLoad();
        return executor;
    }

    @Override
    public void returnExecutor(OrderedEventExecutor executor) {
        executor.decreaseLoad();
    }

    @Override
    public int getExecutorTotal() {
        return this.count;
    }

    /**
     * 根据一个值来算出一个索引值。
     * 这个索引值对应到{@link #pool}。
     *
     * @param value 用来计算索引值的数值
     * @return 索引值
     */
    private int hash(int value) {
        return Math.abs(value * 31) % this.pool.length;
    }

    @Override
    public void start() {
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setNameFormat(this.prefix + "-%d");

        Thread.UncaughtExceptionHandler handler = (Thread t, Throwable e) -> {
            logger.error("执行器[{}]在处理任务时出现了未捕获的错误", t.getName(), e);
        };

        threadFactoryBuilder.setUncaughtExceptionHandler(handler);
        ThreadFactory threadFactory = threadFactoryBuilder.build();

        this.pool = new OrderedEventExecutor[this.count];
        for (int index = 0; index < this.count; index++) {
            OrderedEventExecutor executor = new OrderedEventExecutor();
            this.pool[index] = executor;

            Thread thread = threadFactory.newThread(executor);
            executor.setGroup(this);
            executor.start(thread);
        }
    }

    @Override
    public void stop() {
        for (OrderedEventExecutor executor : this.pool) {
            executor.stop();
        }
        this.pool = null;
    }

    public HashBasedEventExecutorGroup(String prefix, int count) {
        if (StringUtils.isEmpty(prefix) || count < 1) {
            throw new IllegalArgumentException("prefix=[" + prefix + "], threadCount=[" + count + "]");
        }

        this.prefix = prefix;
        this.count = count;
    }

}
