package moster.infras.core.executor;

/**
 * @author zhangfei
 */
public interface EventExecutorGroup<T extends EventExecutor> {

    /**
     * 借出一个执行器。
     *
     * @return 执行器，不为null
     */
    T borrowExecutor();

    /**
     * 借出一个执行器。
     *
     * @param arg 参数
     * @return 执行器，不为null
     */
    T borrowExecutor(int arg);

    /**
     * 归还一个执行器。
     *
     * @param executor 被归还的执行器
     */
    void returnExecutor(T executor);

    /**
     * 所有执行器的总数。
     *
     * @return 执行器总数
     */
    int getExecutorTotal();

    /**
     * 启动执行器组。
     */
    void start();

    /**
     * 停止执行器组。
     */
    void stop();

}
