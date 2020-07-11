package moster.infras.core.executor;

import java.util.concurrent.Executor;

/**
 * @author zhangfei
 */
public interface EventExecutor extends Executor {

    /**
     * @return 当前的负载
     */
    int getLoad();

    /**
     * @return 所属的执行器组
     */
    EventExecutorGroup<? extends EventExecutor> getGroup();

    /**
     * 释放执行器。
     * 当使用执行器完毕后，需要将它释放，以便被其它对象使用。
     */
    void release();

}
