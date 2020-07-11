package moster.app;

import moster.app.config.GlobalConfig;
import moster.infras.core.executor.EventExecutor;
import moster.infras.core.executor.EventExecutorGroup;
import moster.infras.core.executor.HashBasedEventExecutorGroup;
import moster.infras.util.YamlUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author zhangfei
 */
public class SimulatorContext {

    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    /**
     * 通用的线程池。
     */
    static ScheduledExecutorService generalThreadPool;

    /**
     * 用于执行玩家逻辑的线程池。
     */
    static EventExecutorGroup<? extends EventExecutor> playerExecutorGroup;

    /**
     * 全局配置。
     */
    static GlobalConfig globalConfig;

    public static void init() {
        SimulatorContext.globalConfig = YamlUtils.load(GlobalConfig.class, "simulator.yaml");

        // 初始化玩家逻辑线程池。
        SimulatorContext.playerExecutorGroup = new HashBasedEventExecutorGroup("playerExecutor", 2);
        SimulatorContext.playerExecutorGroup.start();

        initGeneralThreadPool();
    }

    /**
     * 初始化通用线程池。
     */
    private static void initGeneralThreadPool() {
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setNameFormat("decision-%d");

        Thread.UncaughtExceptionHandler handler = (Thread t, Throwable e) -> {
            logger.error("决策线程[{}]在执行任务时出现了未捕获的错误", t.getName(), e);
        };

        threadFactoryBuilder.setUncaughtExceptionHandler(handler);
        SimulatorContext.generalThreadPool = Executors.newScheduledThreadPool(1, threadFactoryBuilder.build());
    }

    public static ScheduledExecutorService getGeneralThreadPool() {
        return SimulatorContext.generalThreadPool;
    }

    public static EventExecutorGroup<? extends EventExecutor> getPlayerExecutorGroup() {
        return SimulatorContext.playerExecutorGroup;
    }

    public static GlobalConfig getGlobalConfig() {
        return SimulatorContext.globalConfig;
    }

}
